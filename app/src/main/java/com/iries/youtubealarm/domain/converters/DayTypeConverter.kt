package com.iries.youtubealarm.domain.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iries.youtubealarm.domain.constants.Day

class DayTypeConverter {
    @TypeConverter
    fun stringToMap(json: String?): HashMap<Day, Int> {
        return Gson().fromJson(json,
            object : TypeToken<HashMap<Day, Int>>() {})
    }

    @TypeConverter
    fun mapToString(daysId: HashMap<Day, Int>): String {
        return Gson().toJson(daysId)
    }
}