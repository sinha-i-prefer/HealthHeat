package com.example.healthheatv2.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthheatv2.network.FoodResponse
import com.example.healthheatv2.network.RetrofitClient
import kotlinx.coroutines.launch

// Represents the different states of our API call
sealed class ApiState {
    object Idle : ApiState()
    object Loading : ApiState()
    data class Success(val data: FoodResponse) : ApiState()
    data class Error(val message: String) : ApiState()
}

class ScannerViewModel : ViewModel() {
    private val _apiState = mutableStateOf<ApiState>(ApiState.Idle)
    val apiState: State<ApiState> = _apiState

    fun lookupBarcode(barcode: String) {
        _apiState.value = ApiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getFoodData(barcode)
                _apiState.value = ApiState.Success(response)
                Log.d("API_CALL", "Success: ${response.name}")
            } catch (e: Exception) {
                _apiState.value = ApiState.Error(e.message ?: "Unknown error occurred")
                Log.e("API_CALL", "Error fetching data", e)
            }
        }
    }

    fun resetState() {
        _apiState.value = ApiState.Idle
    }
}