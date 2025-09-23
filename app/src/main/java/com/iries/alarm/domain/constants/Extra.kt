package com.iries.alarm.domain.constants

enum class Extra(var extraName: String) {
    /*RINGTONE_NAME_EXTRA("ringtone_name_extra"),
    RINGTONE_URI_EXTRA("ringtone_uri_extra"),
    RINGTONE_AUTHOR_EXTRA("ringtone_author_extra"),
    ALARM_TIME("time_in_millis"),*/
    ALARM_HOUR("alarm_hour"),
    ALARM_MINUTE("alarm_minute"),
    ALARM_DAY("alarm_day"),
    ALARM_CODE("alarm_code"),
    IS_ALARM_REPEATING("is_alarm_repeating");
}