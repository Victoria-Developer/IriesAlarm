package com.iries.alarm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "ARTISTS")
class ArtistEntity(
    private var username: String? = null,
    private var imgUrl: String? = null,
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    private var id: Long = 0

    fun setId(id: Long) {
        this.id = id
    }

    fun getId(): Long {
        return id
    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun getUsername(): String? {
        return username
    }

    fun setImgUrl(imgUrl: String) {
        this.imgUrl = imgUrl
    }

    fun getImgUrl(): String? {
        return imgUrl
    }

}