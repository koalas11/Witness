package org.wdsl.witness

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import org.wdsl.witness.model.GoogleOAuth
import org.wdsl.witness.repository.GoogleAccountRepositoryImpl
import org.wdsl.witness.repository.RecordingsRepository
import org.wdsl.witness.repository.RecordingsRepositoryImpl
import org.wdsl.witness.repository.SettingsRepository
import org.wdsl.witness.repository.SettingsRepositoryImpl
import org.wdsl.witness.service.GoogleDriveServiceImpl
import org.wdsl.witness.service.GoogleGmailServiceImpl
import org.wdsl.witness.service.GoogleOAuthServiceImpl
import org.wdsl.witness.service.GoogleProfileServiceImpl
import org.wdsl.witness.storage.datastore.GOOGLE_OAUTH_DATASTORE_NAME
import org.wdsl.witness.storage.datastore.GOOGLE_PROFILE_DATASTORE_NAME
import org.wdsl.witness.storage.datastore.GoogleOAuthSerializer
import org.wdsl.witness.storage.datastore.GoogleProfileSerializer
import org.wdsl.witness.storage.datastore.SETTINGS_DATASTORE_NAME
import org.wdsl.witness.storage.datastore.SettingsSerializer
import org.wdsl.witness.storage.datastore.getDataStore
import org.wdsl.witness.storage.room.WitnessDatabase
import org.wdsl.witness.usecase.GoogleIntegrationUseCase
import org.wdsl.witness.util.Log

/**
 * Application Container interface for Dependency Injection
 */
interface AppContainer {
    val settingsRepository: SettingsRepository

    val recordingsRepository: RecordingsRepository

    val httpClient: HttpClient

    val googleOAuthHttpClient: HttpClient

    val googleIntegrationUseCase: GoogleIntegrationUseCase
}

/**
 * Implementation of the AppContainer interface
 * @param platformContext The platform-specific context
 */
open class AppContainerImpl(
    private val platformContext: PlatformContext,
): AppContainer {
    override val settingsRepository: SettingsRepository by lazy {
        val settingsDataStore = getDataStore(platformContext, SETTINGS_DATASTORE_NAME,
            SettingsSerializer)
        SettingsRepositoryImpl(settingsDataStore)
    }

    override val recordingsRepository: RecordingsRepository by lazy {
        val db = WitnessDatabase.getDatabase(platformContext)
        RecordingsRepositoryImpl(db.recordingDao())
    }

    private val googleAccountRepository by lazy {
        val googleProfileDataStore = getDataStore(
            platformContext,
            GOOGLE_PROFILE_DATASTORE_NAME,
            GoogleProfileSerializer
        )
        val googleOauthDataStore = getDataStore(
            platformContext,
            GOOGLE_OAUTH_DATASTORE_NAME,
            GoogleOAuthSerializer
        )
        GoogleAccountRepositoryImpl(
            googleOauthDataStore,
            googleProfileDataStore
        )
    }

    private val googleOAuthService by lazy {
        GoogleOAuthServiceImpl(httpClient)
    }

    override val httpClient: HttpClient by lazy {
        HttpClient {

        }
    }

    override val googleOAuthHttpClient: HttpClient by lazy {
        HttpClient {
            install(Auth) {
                bearer {
                    loadTokens {
                        var bearerTokens: BearerTokens? = null
                        googleAccountRepository.getGoogleOAuth()
                            .onSuccess { googleOAuth ->
                                if (googleOAuth == null) {
                                    Log.d("", "No Google OAuth available for HTTP client bearer token")
                                    return@onSuccess
                                }
                                bearerTokens = BearerTokens(
                                    accessToken = googleOAuth.accessToken,
                                    refreshToken = googleOAuth.refreshToken
                                )
                            }
                        if (bearerTokens == null) {
                            Log.d("", "No Google OAuth available for HTTP client bearer token")
                            throw Exception("No Google OAuth available for HTTP client bearer token")
                        }
                        bearerTokens
                    }
                    refreshTokens {
                        var googleOAuth: GoogleOAuth? = null
                        googleAccountRepository.getGoogleOAuth()
                            .onSuccess { it ->
                                if (it == null) {
                                    Log.d("", "No Google OAuth available for HTTP client bearer token")
                                    return@onSuccess
                                }
                                googleOAuth = it
                            }
                        if (googleOAuth == null) {
                            Log.d("", "No Google OAuth available for HTTP client bearer token")
                            throw Exception("No Google OAuth available for HTTP client bearer token")
                        }
                        var bearerTokens: BearerTokens? = null
                        googleOAuthService.refreshGoogleOAuth(googleOAuth)
                            .onError {
                                Log.d("", "Failed to refresh Google OAuth tokens: ${it.message}")
                                bearerTokens = null
                            }
                            .onSuccess {
                                googleAccountRepository.updateGoogleOAuth(it)
                                bearerTokens = BearerTokens(
                                    accessToken = it.accessToken,
                                    refreshToken = it.refreshToken
                                )
                            }
                        bearerTokens
                    }
                }
            }
        }
    }

    override val googleIntegrationUseCase: GoogleIntegrationUseCase by lazy {
        GoogleIntegrationUseCase(
            googleAccountRepository,
            googleOAuthService,
            GoogleProfileServiceImpl(googleOAuthHttpClient),
            GoogleGmailServiceImpl(googleOAuthHttpClient),
            GoogleDriveServiceImpl(googleOAuthHttpClient),
        )
    }
}
