package com.iries.youtubealarm.domain.constants

enum class Extra(var extraName: String) {
    RINGTONE_NAME_EXTRA("ringtone_name_extra"),
    ALARM_TIME("time_in_millis"),
    ALARM_ID("alarm_id"),
    IS_ALARM_REPEATING("is_alarm_repeating");
}