package com.iries.alarm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ALARMS")
class AlarmEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var days: String = "" // Json string
    var isActive: Boolean = false
    var hour: Int = 0
    var minute: Int = 0
    var isRepeating: Boolean = true
}