package com.clawstack.carabaseadmin.ui.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.clawstack.carabaseadmin.data.CaraBaseSystemRepository
import com.clawstack.carabaseadmin.data.network.SystemStatsData
import com.clawstack.carabaseadmin.data.network.TelemetryResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: CaraBaseSystemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun fetchStats() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val telemetryResult = repository.getSystemStats()
            val aggregateResult = repository.getAggregateSystemStats()

            if (telemetryResult.isSuccess && aggregateResult.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    stats = telemetryResult.getOrNull(),
                    aggregateStats = aggregateResult.getOrNull()?.data,
                    error = null
                )
            } else {
                val errorMsg = telemetryResult.exceptionOrNull()?.message 
                    ?: aggregateResult.exceptionOrNull()?.message 
                    ?: "Failed to fetch stats."
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = errorMsg
                )
            }
        }
    }
}

data class DashboardUiState(
    val stats: TelemetryResponse? = null,
    val aggregateStats: SystemStatsData? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class DashboardViewModelFactory(private val repository: CaraBaseSystemRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
