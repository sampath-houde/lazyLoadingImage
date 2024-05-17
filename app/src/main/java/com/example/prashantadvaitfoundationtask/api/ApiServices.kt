package com.example.prashantadvaitfoundationtask.api

import com.example.prashantadvaitfoundationtask.data.model.MainResponse
import retrofit2.http.GET

interface ApiServices {
    @GET("api/v2/content/misc/media-coverages?limit=100")
    suspend fun getMainData(): List<MainResponse>
}