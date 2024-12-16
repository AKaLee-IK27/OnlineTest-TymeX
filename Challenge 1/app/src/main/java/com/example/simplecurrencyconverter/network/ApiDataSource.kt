package com.example.simplecurrencyconverter.network

import javax.inject.Inject

class ApiDataSource @Inject constructor(private val apiService: ApiService) {
    suspend fun getLatestRates() = apiService.getLatestRates()
}

