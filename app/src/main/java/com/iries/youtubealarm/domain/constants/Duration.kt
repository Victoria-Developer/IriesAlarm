package com.iries.youtubealarm.domain.constants

enum class Duration(private var durationName: String?) {
    ANY("any"),
    SHORT("short"),
    MEDIUM("medium"),
    LONG("long");

    fun getDurationName(): String? {
        return durationName
    }

    override fun toString(): String {
        return durationName!!
    }
}