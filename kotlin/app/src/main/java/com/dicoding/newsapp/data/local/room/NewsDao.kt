package com.dicoding.newsapp.data.local.room

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.dicoding.newsapp.data.local.entity.NewsEntity

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNews(news: List<NewsEntity>) {
        Log.d("NewsDao", "Inserting ${news.size} news articles.")
        // Penyisipan data ke dalam tabel
        insertNewsInternal(news)
    }

    @Query("DELETE FROM news WHERE bookmarked = 0")
    fun deleteAll() {
        Log.d("NewsDao", "Deleting all news that are not bookmarked.")
        // Menghapus data dari tabel
        deleteAllInternal()
    }

    // Fungsi internal untuk menyisipkan data (ini adalah metode private yang sebenarnya)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNewsInternal(news: List<NewsEntity>)

    // Fungsi internal untuk menghapus data (ini adalah metode private yang sebenarnya)
    @Query("DELETE FROM news WHERE bookmarked = 0")
    fun deleteAllInternal()

    // Fungsi-fungsi lain yang Anda perlukan di DAO
    @Query("SELECT * FROM news ORDER BY publishedAt DESC")
    fun getNews(): LiveData<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE bookmarked = 1")
    fun getBookmarkedNews(): LiveData<List<NewsEntity>>

    @Update
    fun updateNews(news: NewsEntity)

    @Query("SELECT EXISTS(SELECT * FROM news WHERE title = :title AND bookmarked = 1)")
    fun isNewsBookmarked(title: String): Boolean
}

