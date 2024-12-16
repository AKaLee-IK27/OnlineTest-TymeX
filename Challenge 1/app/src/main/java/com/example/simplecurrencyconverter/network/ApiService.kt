package com.example.simplecurrencyconverter.network

import com.example.simplecurrencyconverter.helper.EndPoints
import com.example.simplecurrencyconverter.model.ApiResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("${EndPoints.BASE_URL}latest.json/?app_id=${EndPoints.API_KEY}")
    suspend fun getLatestRates(): Response<ApiResponse>
}