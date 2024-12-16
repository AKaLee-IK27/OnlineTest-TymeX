package com.example.simplecurrencyconverter.viewmodel

import com.example.simplecurrencyconverter.helper.Resource
import com.example.simplecurrencyconverter.model.ApiResponse
import com.example.simplecurrencyconverter.network.ApiDataSource
import com.ibrajix.directcurrencyconverter.network.BaseDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainRepo @Inject constructor(private val apiDataSource: ApiDataSource) : BaseDataSource() {

    suspend fun getLatestRates(): Flow<Resource<ApiResponse>> {
        return flow<Resource<ApiResponse>> {
            emit(safeApiCall { apiDataSource.getLatestRates() })
        }.flowOn(Dispatchers.IO)
    }

}