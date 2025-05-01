package com.example.dicodingapp.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingapp.data.FavoriteEventRepository
import com.example.dicodingapp.ui.viewmodel.EventDetailViewModel

class EventDetailViewModelFactory(private val favoriteEventRepository: FavoriteEventRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventDetailViewModel(favoriteEventRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
