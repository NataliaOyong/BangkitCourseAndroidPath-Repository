package com.example.dicodingapp.ui.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingapp.data.response.ListEventsItem
import com.example.dicodingapp.data.response.EventResponse
import com.example.dicodingapp.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventViewModel : ViewModel() {

    private val _activeEvents = MutableLiveData<List<ListEventsItem>>()
    val activeEvents: LiveData<List<ListEventsItem>> = _activeEvents

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    companion object {
        private const val TAG = "com.example.dicodingapp.ui.viewmodel.EventViewModel"
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)
        }
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    fun fetchActiveEvents(context: Context) {
        if (!isInternetAvailable(context)) {
            _errorMessage.value = "No internet connection."
            return
        }

        _isLoading.value = true
        val client = ApiConfig.getApiService().getEvents(active = 1)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        _activeEvents.value = responseBody.listEvents
                    } ?: run {
                        _errorMessage.value = "No active events found."
                        Log.e(TAG, "Response body is null")
                    }
                } else {
                    _errorMessage.value = "Failed to fetch active events: ${response.message()}"
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error: ${t.message}"
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun fetchFinishedEvents(context: Context) {
        if (!isInternetAvailable(context)) {
            _errorMessage.value = "No internet connection."
            return
        }

        _isLoading.value = true
        val client = ApiConfig.getApiService().getEvents(active = 0)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        _finishedEvents.value = responseBody.listEvents
                    } ?: run {
                        _errorMessage.value = "No finished events found."
                        Log.e(TAG, "Response body is null")
                    }
                } else {
                    _errorMessage.value = "Failed to fetch finished events: ${response.message()}"
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error: ${t.message}"
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}
