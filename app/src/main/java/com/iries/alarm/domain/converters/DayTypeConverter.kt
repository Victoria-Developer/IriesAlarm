package com.iries.alarm.domain.converters

import androidx.room.TypeConverter
import com.iries.alarm.domain.constants.Day
import com.iries.alarm.domain.constants.DayIdSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class DayTypeConverter {

    private val json = Json { encodeDefaults = true }

    private val mapSerializer = MapSerializer(DayIdSerializer, Int.serializer())

    @TypeConverter
    fun stringToMap(jsonString: String?): HashMap<Day, Int> {
        if (jsonString.isNullOrEmpty()) return hashMapOf()
        val map: Map<Day, Int> = json.decodeFromString(mapSerializer, jsonString)
        return HashMap(map)
    }

    @TypeConverter
    fun mapToString(daysId: HashMap<Day, Int>): String {
        return json.encodeToString(mapSerializer, daysId)
    }
}