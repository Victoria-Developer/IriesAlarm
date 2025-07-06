package com.iries.youtubealarm.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.iries.youtubealarm.domain.constants.Day
import com.iries.youtubealarm.domain.converters.DayTypeConverter
import java.util.Locale

@Entity(tableName = "ALARMS")
class AlarmInfo {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    private var isActive = false
    private var hour: Int = 0
    private var minute: Int = 0

    @TypeConverters(DayTypeConverter::class)
    private var daysId: HashMap<Day, Int> = HashMap(7)

    fun setTime(hour: Int, minute: Int) {
        this.hour = hour
        this.minute = minute
    }

    fun setHour(hour: Int) {
        this.hour = hour
    }

    fun getHour(): Int {
        return hour
    }

    fun setMinute(minute: Int) {
        this.minute = minute
    }

    fun getMinute(): Int {
        return minute
    }

    fun getFormattedTime(): String {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
    }

    fun setAlarmId(id: Long) {
        this.id = id
    }

    fun getAlarmId(): Long {
        return id
    }

    fun setActive(active: Boolean) {
        isActive = active
    }

    fun isActive(): Boolean {
        return isActive
    }

    fun setDaysId(daysId: HashMap<Day, Int>) {
        this.daysId = daysId
    }

    fun getDaysId(): HashMap<Day, Int> {
        return daysId
    }
}