package org.wdsl.witness.storage.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.wdsl.witness.model.LlmSummary
import org.wdsl.witness.model.LocationData

/**
 * Data class representing a recording entity in the Room database.
 */
@Entity
data class Recording(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val recordingFileName: String,
    val gpsPositions: List<LocationData>,
    val llmSummary: LlmSummary? = null,
)
