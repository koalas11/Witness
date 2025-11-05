package org.wdsl.witness.storage.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Duration

/**
 * Data class representing a recording entity in the Room database.
 */
@Entity
data class Recording(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val data: ByteArray,
    val durationMs: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Recording

        if (id != other.id) return false
        if (title != other.title) return false
        if (!data.contentEquals(other.data)) return false
        if (durationMs != other.durationMs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + data.contentHashCode()
        result = 31 * result + durationMs.hashCode()
        return result
    }
}
