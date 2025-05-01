package com.example.dicodingapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingapp.data.FavoriteEventRepository
import com.example.dicodingapp.data.local.entity.FavoriteEvent

class FavoriteViewModel(private val repository: FavoriteEventRepository) : ViewModel() {

    val favorites: LiveData<List<FavoriteEvent>> = repository.getFavorites()
}