package com.iries.youtubealarm.domain.models

class Alarm {
    var id: Long = 0
    var groupId: Long = 0 // Same time, different days
    var dayId: Int = 0 // 0 for Monday, 1 for Tuesday etc
    var isActive: Boolean = false
    var hour: Int = 0
    var minute: Int = 0
}