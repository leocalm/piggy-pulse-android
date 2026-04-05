package com.piggypulse.android.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piggypulse.android.core.repository.DashboardData
import com.piggypulse.android.core.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository,
    val layout: DashboardLayout,
) : ViewModel() {

    private val _data = MutableStateFlow<DashboardData?>(null)
    val data: StateFlow<DashboardData?> = _data.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _showCustomize = MutableStateFlow(false)
    val showCustomize: StateFlow<Boolean> = _showCustomize.asStateFlow()

    private var loadJob: Job? = null

    fun load(periodId: String) {
        loadJob?.cancel()
        _isLoading.value = true
        _errorMessage.value = null
        loadJob = viewModelScope.launch {
            repository.fetchAll(periodId).fold(
                onSuccess = { _data.value = it },
                onFailure = { _errorMessage.value = "Failed to load dashboard" },
            )
            _isLoading.value = false
        }
    }

    fun openCustomize() { _showCustomize.value = true }
    fun closeCustomize() { _showCustomize.value = false }
}
