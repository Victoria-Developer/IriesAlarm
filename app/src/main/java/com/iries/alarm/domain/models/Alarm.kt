package com.iries.alarm.domain.models

class Alarm (
    var id: Long = 0,
    var days: HashMap<Int, Int> = hashMapOf(), // dayId (Mon, Fri etc) - alarm intent id
    var isActive: Boolean = false,
    var hour: Int = 0,
    var minute: Int = 0
)