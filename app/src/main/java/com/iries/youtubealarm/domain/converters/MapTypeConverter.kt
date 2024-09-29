package com.iries.youtubealarm.domain.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iries.youtubealarm.domain.constants.DayOfWeek

class MapTypeConverter {
    @TypeConverter
    fun stringToMap(json: String?): HashMap<DayOfWeek, Int> {
        return Gson().fromJson(json,
            object : TypeToken<HashMap<DayOfWeek, Int>>() {})
    }

    @TypeConverter
    fun mapToString(daysId: HashMap<DayOfWeek, Int>): String {
        return Gson().toJson(daysId)
    }
}