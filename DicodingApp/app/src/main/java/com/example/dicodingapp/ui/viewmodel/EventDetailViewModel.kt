package com.example.dicodingapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingapp.data.FavoriteEventRepository
import com.example.dicodingapp.data.local.entity.FavoriteEvent
import com.example.dicodingapp.data.response.DetailEventResponse
import com.example.dicodingapp.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventDetailViewModel(private val favoriteEventRepository: FavoriteEventRepository) : ViewModel() {

    private val _eventDetail = MutableLiveData<DetailEventResponse?>()
    val eventDetail: LiveData<DetailEventResponse?> get() = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    val favorites: LiveData<List<FavoriteEvent>> = favoriteEventRepository.getFavorites()

    fun checkIfFavorite(eventId: Int) {
        viewModelScope.launch {
            favoriteEventRepository.getFavoriteById(eventId).observeForever { favoriteEvent ->
                _isFavorite.postValue(favoriteEvent != null)
            }
        }
    }

    fun getEventDetail(eventId: String) {
        _isLoading.value = true
        ApiConfig.getApiService().getEventDetail(eventId).enqueue(object : Callback<DetailEventResponse> {
            override fun onResponse(call: Call<DetailEventResponse>, response: Response<DetailEventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _eventDetail.value = response.body()
                } else {
                    Log.e("EventDetailViewModel", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("EventDetailViewModel", "Failure: ${t.message}")
                _eventDetail.value = null
            }
        })
    }

    fun addFavorite(favoriteEvent: FavoriteEvent) {
        viewModelScope.launch {
            favoriteEventRepository.addFavorite(favoriteEvent)
            _isFavorite.value = true
        }
    }

    fun removeFavorite(eventId: Int) {
        viewModelScope.launch {
            favoriteEventRepository.removeFavorite(eventId)
            _isFavorite.value = false
        }
    }
}
