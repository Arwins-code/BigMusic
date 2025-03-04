package com.arwin.bigmusic.data.network

import com.arwin.bigmusic.data.model.MusicResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

interface ApiService {
    @GET("search")
    fun searchMusic(@Query("term") term: String): Call<MusicResponse>
}