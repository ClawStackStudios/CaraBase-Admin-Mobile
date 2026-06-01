package com.clawstack.carabaseadmin.ui.feature.gateway

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.clawstack.carabaseadmin.data.CaraBaseSystemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GatewayViewModel(
    private val repository: CaraBaseSystemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GatewayUiState(
        url = repository.getLastUrl() ?: "https://"
    ))
    val uiState: StateFlow<GatewayUiState> = _uiState.asStateFlow()

    fun updateUrl(url: String) {
        _uiState.value = _uiState.value.copy(url = url, error = null)
    }

    fun updateToken(token: String) {
        _uiState.value = _uiState.value.copy(adminToken = token, error = null)
    }

    fun authenticate(onSuccess: () -> Unit) {
        val currentUrl = _uiState.value.url
        val currentToken = _uiState.value.adminToken

        if (currentUrl.isBlank() || currentToken.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "URL and Token are required.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            repository.setBaseUrl(currentUrl)
            val result = repository.authenticate(currentToken)
            
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = true)
                onSuccess()
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Authentication failed."
                )
            }
        }
    }
}

data class GatewayUiState(
    val url: String = "https://",
    val adminToken: String = "",
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null
)

class GatewayViewModelFactory(private val repository: CaraBaseSystemRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GatewayViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GatewayViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
