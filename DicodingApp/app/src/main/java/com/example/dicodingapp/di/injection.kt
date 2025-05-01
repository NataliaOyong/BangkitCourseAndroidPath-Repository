package com.example.dicodingapp.di

import android.content.Context
import com.example.dicodingapp.data.FavoriteEventRepository
import com.example.dicodingapp.data.local.room.FavoriteEventDatabase
import com.example.dicodingapp.data.local.room.FavoriteEventDao
import com.example.dicodingapp.data.retrofit.ApiConfig

object Injection {
    fun provideFavoriteEventRepository(context: Context): FavoriteEventRepository {
        ApiConfig.getApiService()
        val database = FavoriteEventDatabase.getDatabase(context)
        val favoriteEventDao: FavoriteEventDao = database.favoriteEventDao()
        return FavoriteEventRepository(favoriteEventDao)
    }
}
