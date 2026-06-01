package com.clawstack.carabaseadmin.ui.feature.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.clawstack.carabaseadmin.data.CaraBaseSystemRepository
import com.clawstack.carabaseadmin.data.network.UserMetadata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UsersUiState(
    val users: List<UserMetadata> = emptyList(),
    val isLoading: Boolean = false,
    val isMoreLoading: Boolean = false,
    val totalUsers: Int = 0,
    val error: String? = null,
    val searchQuery: String = "",
    val deleteError: String? = null
)

class UsersViewModel(private val repository: CaraBaseSystemRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    fun fetchUsers(offset: Int = 0) {
        if (offset == 0) {
            _uiState.update { it.copy(isLoading = true, error = null) }
        } else {
            _uiState.update { it.copy(isMoreLoading = true) }
        }

        viewModelScope.launch {
            val result = repository.getUsers(limit = 50, offset = offset)
            result.onSuccess { response ->
                _uiState.update { currentState ->
                    val newUsers = if (offset == 0) response.data else currentState.users + response.data
                    currentState.copy(
                        isLoading = false,
                        isMoreLoading = false,
                        users = newUsers,
                        totalUsers = response.pagination?.total ?: 0
                    )
                }
            }.onFailure { e ->
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        isMoreLoading = false,
                        error = e.message ?: "Failed to fetch users"
                    ) 
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun deleteUser(uuid: String) {
        viewModelScope.launch {
            val result = repository.deleteUser(uuid)
            result.onSuccess {
                // Remove the user from local state to reflect UI instantly
                _uiState.update { state ->
                    state.copy(users = state.users.filter { it.uuid != uuid }, deleteError = null)
                }
            }.onFailure { e ->
                _uiState.update { it.copy(deleteError = e.message ?: "Delete operation failed") }
            }
        }
    }

    fun clearDeleteError() {
        _uiState.update { it.copy(deleteError = null) }
    }
}

class UsersViewModelFactory(
    private val repository: CaraBaseSystemRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsersViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
