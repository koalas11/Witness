package org.wdsl.witness

import org.wdsl.witness.repository.GoogleAccountRepositoryImpl
import org.wdsl.witness.repository.RecordingsRepository
import org.wdsl.witness.repository.RecordingsRepositoryImpl
import org.wdsl.witness.repository.SettingsRepository
import org.wdsl.witness.repository.SettingsRepositoryImpl
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

/**
 * Application Container interface for Dependency Injection
 */
interface AppContainer {
    val settingsRepository: SettingsRepository

    val recordingsRepository: RecordingsRepository

    val googleIntegrationUseCase: GoogleIntegrationUseCase
}

/**
 * Implementation of the AppContainer interface
 * @param platformContext The platform-specific context
 */
open class AppContainerImpl(
    private val platformContext: PlatformContext
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

    override val googleIntegrationUseCase: GoogleIntegrationUseCase by lazy {
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

        GoogleIntegrationUseCase(
            GoogleAccountRepositoryImpl(googleOauthDataStore, googleProfileDataStore),
            GoogleOAuthServiceImpl(),
            GoogleProfileServiceImpl(),
        )
    }
}
