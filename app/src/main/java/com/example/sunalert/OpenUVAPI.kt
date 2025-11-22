package com.example.sunalert

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface OpenUVAPI {
    @GET("uv")
    suspend fun getUV(
        @Header("x-access-token") apiKey: String,
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): UVResponse
}