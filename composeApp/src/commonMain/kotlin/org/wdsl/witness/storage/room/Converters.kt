package org.wdsl.witness.storage.room

import androidx.room.TypeConverter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import org.wdsl.witness.model.LlmSummary
import org.wdsl.witness.model.LocationData

@OptIn(ExperimentalSerializationApi::class)
class Converters {
    @TypeConverter
    fun fromLocationDataList(locationData: List<LocationData>?): ByteArray? {
        if (locationData == null)
            return null
        return ProtoBuf.encodeToByteArray(locationData)
    }

    @TypeConverter
    fun byteArrayToLocationDataList(byteArray: ByteArray?): List<LocationData>? {
        if (byteArray == null)
            return null
        return ProtoBuf.decodeFromByteArray(ListSerializer(LocationData.serializer()), byteArray)
    }

    @TypeConverter
    fun fromLlmSummary(summary: LlmSummary?): String? {
        if (summary == null) {
            return null
        }
        return Json.encodeToString(summary)
    }

    @TypeConverter
    fun stringToLlmSummary(summary: String?): LlmSummary? {
        if (summary == null) {
            return null
        }
        return Json.decodeFromString(summary)
    }
}
