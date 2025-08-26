package com.iries.alarm.domain.constants


enum class Day(var id: Int, var weekName: String?) {
    SUNDAY(1, "Sunday"),
    MONDAY(2, "Monday"),
    TUESDAY(3, "Tuesday"),
    WEDNESDAY(4, "Wednesday"),
    THURSDAY(5, "Thursday"),
    FRIDAY(6, "Friday"),
    SATURDAY(7, "Saturday");

    companion object {
        fun getById(id: Int): Day {
            return entries.first { it.id == id }
        }
    }
}