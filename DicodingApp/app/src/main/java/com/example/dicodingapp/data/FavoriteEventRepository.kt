package com.example.dicodingapp.data

import androidx.lifecycle.LiveData
import com.example.dicodingapp.data.local.entity.FavoriteEvent
import com.example.dicodingapp.data.local.room.FavoriteEventDao

class FavoriteEventRepository(
    private val favoriteEventDao: FavoriteEventDao
) {

    companion object {
        private var instance: FavoriteEventRepository? = null

        fun getInstance(favoriteEventDao: FavoriteEventDao): FavoriteEventRepository {
            return instance ?: synchronized(this) {
                instance ?: FavoriteEventRepository(favoriteEventDao).also { instance = it }
            }
        }
    }

    fun getFavorites(): LiveData<List<FavoriteEvent>> {
        return favoriteEventDao.getAllFavorites()
    }

    suspend fun addFavorite(event: FavoriteEvent) {
        favoriteEventDao.insertFavorite(event)
    }

    suspend fun removeFavorite(eventId: Int) {
        favoriteEventDao.deleteFavorite(eventId)
    }

    fun getFavoriteById(eventId: Int): LiveData<FavoriteEvent?> {
        return favoriteEventDao.getFavoriteById(eventId)
    }
}
