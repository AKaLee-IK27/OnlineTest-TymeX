@file:Suppress("DEPRECATION")

package com.example.simplecurrencyconverter.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplecurrencyconverter.helper.Resource
import com.example.simplecurrencyconverter.helper.SingleLiveEvent
import com.example.simplecurrencyconverter.model.ApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepo: MainRepo) : ViewModel() {

    private val _data = SingleLiveEvent<Resource<ApiResponse>>()


    val data = _data
    val convertedRate = MutableLiveData<Double>()

    fun fetchLatestRates() {
        viewModelScope.launch {
            mainRepo.getLatestRates().collect {
                data.value = it
            }
        }
    }

    fun convertToUSD(from: String, amount: Double) {
       convertedRate
    }

    fun convertCurrency(from: String, to: String, amount: Double) {

    }
}