package com.iries.youtubealarm.domain.constants

enum class ORDER(private val orderName: String?) {
    DATE("date"),
    RATING("rating"),
    VIEW_COUNT("viewCount");

    fun getOrderName(): String? {
        return orderName
    }

    override fun toString(): String {
        return orderName!!
    }
}