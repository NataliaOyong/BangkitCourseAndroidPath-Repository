package com.example.myrecyclerview

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Planet(
    val name: String,
    val description: String,
    val photo: Int
) : Parcelable
