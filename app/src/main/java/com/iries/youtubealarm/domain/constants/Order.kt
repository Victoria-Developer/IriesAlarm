package com.iries.youtubealarm.domain.constants

enum class Order(private val orderName: String?) {
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