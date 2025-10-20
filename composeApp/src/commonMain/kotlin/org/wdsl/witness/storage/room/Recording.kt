package org.wdsl.witness.storage.room

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a recording entity in the Room database.
 */
@Entity
data class Recording(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
)
