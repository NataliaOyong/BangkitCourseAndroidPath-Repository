package com.example.dicodingapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dicodingapp.data.local.entity.FavoriteEvent

@Dao
interface FavoriteEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(event: FavoriteEvent)

    @Query("DELETE FROM favorite_events WHERE id = :eventId")
    suspend fun deleteFavorite(eventId: Int)

    @Query("SELECT * FROM favorite_events WHERE id = :eventId LIMIT 1")
    fun getFavoriteById(eventId: Int): LiveData<FavoriteEvent?>

    @Query("SELECT * FROM favorite_events")
    fun getAllFavorites(): LiveData<List<FavoriteEvent>>
}
