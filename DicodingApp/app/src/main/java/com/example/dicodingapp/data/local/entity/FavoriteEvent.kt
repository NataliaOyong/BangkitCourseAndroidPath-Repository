package com.example.dicodingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_events")
data class FavoriteEvent(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val description: String
)
