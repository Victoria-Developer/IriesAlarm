package com.iries.youtubealarm.domain.constants

import java.util.Arrays

enum class DayOfWeek(private var id: Int, private var weekName: String?) {
    SUNDAY(1, "Sunday"),
    MONDAY(2, "Monday"),
    TUESDAY(3, "Tuesday"),
    WEDNESDAY(4, "Wednesday"),
    THURSDAY(5, "Thursday"),
    FRIDAY(6,"Friday"),
    SATURDAY(7, "Saturday");

    fun getId(): Int {
        return id
    }

    fun getName(): String? {
        return weekName
    }

    fun getById(id: Int): DayOfWeek {
        return Arrays.stream(entries.toTypedArray())
            .filter { day: DayOfWeek -> day.id == id }
            .findFirst().get()
    }
}