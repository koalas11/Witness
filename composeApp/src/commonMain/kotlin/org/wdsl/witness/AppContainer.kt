package org.wdsl.witness

import org.wdsl.witness.repository.RecordingsRepository
import org.wdsl.witness.repository.RecordingsRepositoryImpl
import org.wdsl.witness.repository.SettingsRepository
import org.wdsl.witness.repository.SettingsRepositoryImpl
import org.wdsl.witness.storage.datastore.getSettingsDataStore
import org.wdsl.witness.storage.room.WitnessDatabase

/**
 * Application Container interface for Dependency Injection
 */
interface AppContainer {
    val settingsRepository: SettingsRepository

    val recordingsRepository: RecordingsRepository
}

/**
 * Implementation of the AppContainer interface
 * @param platformContext The platform-specific context
 */
open class AppContainerImpl(
    private val platformContext: PlatformContext,
): AppContainer {
    override val settingsRepository: SettingsRepository by lazy {
        val settingsDataStore = getSettingsDataStore(platformContext)
        SettingsRepositoryImpl(settingsDataStore)
    }

    override val recordingsRepository: RecordingsRepository by lazy {
        val db = WitnessDatabase.getDatabase(platformContext)
        RecordingsRepositoryImpl(db.recordingDao())
    }
}
