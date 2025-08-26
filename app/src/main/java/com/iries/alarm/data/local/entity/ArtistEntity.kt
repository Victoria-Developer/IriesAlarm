package com.iries.alarm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "ARTISTS")
class ArtistEntity : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var username: String? = null
    var imgUrl: String? = null
}