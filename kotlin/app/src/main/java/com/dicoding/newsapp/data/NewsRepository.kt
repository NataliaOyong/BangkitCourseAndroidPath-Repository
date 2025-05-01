package com.dicoding.newsapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dicoding.newsapp.BuildConfig
import com.dicoding.newsapp.data.local.entity.NewsEntity
import com.dicoding.newsapp.data.local.room.NewsDao
import com.dicoding.newsapp.data.remote.response.NewsResponse
import com.dicoding.newsapp.data.remote.retrofit.ApiService
import com.dicoding.newsapp.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsRepository private constructor(
    private val apiService: ApiService,
    private val newsDao: NewsDao,
    private val appExecutors: AppExecutors
) {
    private val result = MediatorLiveData<Result<List<NewsEntity>>>()

    fun getHeadlineNews(): LiveData<Result<List<NewsEntity>>> {
        result.value = Result.Loading

        // Mengambil data dari API
        val client = apiService.getNews(BuildConfig.API_KEY)
        client.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                // Tambahkan log untuk memeriksa respons API
                Log.d("API Response", "Response: ${response.body()}")

                if (response.isSuccessful) {
                    val articles = response.body()?.articles
                    val newsList = ArrayList<NewsEntity>()
                    appExecutors.diskIO.execute {
                        articles?.forEach { article ->
                            val isBookmarked = newsDao.isNewsBookmarked(article.title)
                            val news = NewsEntity(
                                title = article.title,
                                publishedAt = article.publishedAt,
                                urlToImage = article.urlToImage,
                                url = article.url,
                                isBookmarked = isBookmarked
                            )
                            newsList.add(news)
                        }

                        // Hapus semua berita sebelum menyisipkan yang baru
                        newsDao.deleteAll()
                        newsDao.insertNews(newsList)
                    }
                } else {
                    // Menangani kesalahan respons jika tidak berhasil
                    result.value = Result.Error("Response error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                // Tambahkan log untuk menangani kesalahan
                Log.e("API Error", "Error: ${t.message}")
                result.value = Result.Error(t.message.toString())
            }
        })

        // Ambil data dari database
        val localData = newsDao.getNews()
        result.addSource(localData) { newData: List<NewsEntity> ->
            result.value = Result.Success(newData)
        }

        return result
    }

    companion object {
        @Volatile
        private var instance: NewsRepository? = null
        fun getInstance(
            apiService: ApiService,
            newsDao: NewsDao,
            appExecutors: AppExecutors
        ): NewsRepository =
            instance ?: synchronized(this) {
                instance ?: NewsRepository(apiService, newsDao, appExecutors)
            }.also { instance = it }
    }
}
