package com.example.dicodingapp.data.retrofit

import com.example.dicodingapp.data.response.DetailEventResponse
import com.example.dicodingapp.data.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvents(
        @Query("active") active: Int,
        @Query("keyword") q: String? = null
    ): Call<EventResponse>

    @GET("events/{eventId}")
    fun getEventDetail(
        @Path("eventId") eventId: String
    ): Call<DetailEventResponse>

    @GET("events")
    fun searchEvents(
        @Query("active") active: Int,
        @Query("q") query: String
    ): Call<EventResponse>
}
