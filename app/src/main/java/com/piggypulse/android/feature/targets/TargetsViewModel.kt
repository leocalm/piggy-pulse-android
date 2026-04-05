package com.piggypulse.android.feature.targets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piggypulse.android.core.model.CreateTargetRequest
import com.piggypulse.android.core.model.TargetItem
import com.piggypulse.android.core.model.TargetSummary
import com.piggypulse.android.core.model.UpdateTargetRequest
import com.piggypulse.android.core.repository.TargetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TargetsViewModel @Inject constructor(
    private val repository: TargetRepository,
) : ViewModel() {

    private val _targets = MutableStateFlow<List<TargetItem>>(emptyList())
    val targets: StateFlow<List<TargetItem>> = _targets.asStateFlow()

    private val _summary = MutableStateFlow<TargetSummary?>(null)
    val summary: StateFlow<TargetSummary?> = _summary.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentPeriodId: String? = null
    private var loadJob: Job? = null

    fun load(periodId: String) {
        currentPeriodId = periodId
        loadJob?.cancel()
        _isLoading.value = true
        loadJob = viewModelScope.launch {
            repository.fetchTargets(periodId).onSuccess { response ->
                _summary.value = response.summary
                _targets.value = response.targets
            }
            _isLoading.value = false
        }
    }

    fun createTarget(categoryId: String, value: Long) {
        viewModelScope.launch {
            repository.create(CreateTargetRequest(categoryId, value)).onSuccess {
                currentPeriodId?.let { load(it) }
            }
        }
    }

    fun updateTarget(id: String, value: Long) {
        viewModelScope.launch {
            repository.update(id, UpdateTargetRequest(value)).onSuccess {
                currentPeriodId?.let { load(it) }
            }
        }
    }
}
