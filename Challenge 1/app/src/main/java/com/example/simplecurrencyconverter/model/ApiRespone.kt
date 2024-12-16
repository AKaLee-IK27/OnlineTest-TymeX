package com.example.simplecurrencyconverter.model

data class ApiResponse(
    val base: String,
    val timestamp: Int,
    var rates: Map<String, Double>,
    var license: String,
    val disclaimer: String,
)