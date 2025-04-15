package com.example.conpassunittest1.repo

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("income/{name}")
    suspend fun getIncome(@Path("name") name: String): Double
}