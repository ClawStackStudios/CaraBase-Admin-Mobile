package com.clawstack.carabaseadmin.ui.feature.audit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.clawstack.carabaseadmin.data.CaraBaseSystemRepository
import com.clawstack.carabaseadmin.data.network.AuditLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuditUiState(
    val logs: List<AuditLog> = emptyList(),
    val isLoading: Boolean = false,
    val isMoreLoading: Boolean = false,
    val totalLogs: Int = 0,
    val error: String? = null
)

class AuditViewModel(private val repository: CaraBaseSystemRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuditUiState())
    val uiState: StateFlow<AuditUiState> = _uiState.asStateFlow()

    fun fetchAuditLogs(offset: Int = 0) {
        if (offset == 0) {
            _uiState.update { it.copy(isLoading = true, error = null) }
        } else {
            _uiState.update { it.copy(isMoreLoading = true) }
        }

        viewModelScope.launch {
            val result = repository.getAuditLogs(limit = 50, offset = offset)
            result.onSuccess { response ->
                _uiState.update { currentState ->
                    val newLogs = if (offset == 0) response.data else currentState.logs + response.data
                    currentState.copy(
                        isLoading = false,
                        isMoreLoading = false,
                        logs = newLogs,
                        totalLogs = response.pagination?.total ?: 0
                    )
                }
            }.onFailure { e ->
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        isMoreLoading = false,
                        error = e.message ?: "Failed to fetch audit logs"
                    ) 
                }
            }
        }
    }
}

class AuditViewModelFactory(
    private val repository: CaraBaseSystemRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuditViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
