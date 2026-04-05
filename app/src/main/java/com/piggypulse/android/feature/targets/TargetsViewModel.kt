package com.piggypulse.android.feature.targets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piggypulse.android.core.model.CategoryTargetItem
import com.piggypulse.android.core.model.CreateTargetRequest
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

    private val _targets = MutableStateFlow<List<CategoryTargetItem>>(emptyList())
    val targets: StateFlow<List<CategoryTargetItem>> = _targets.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentPeriodId: String? = null
    private var loadJob: Job? = null

    fun load(periodId: String) {
        currentPeriodId = periodId
        loadJob?.cancel()
        _isLoading.value = true
        loadJob = viewModelScope.launch {
            repository.fetchTargets(periodId).onSuccess { _targets.value = it }
            _isLoading.value = false
        }
    }

    fun createTarget(categoryId: String, amount: Long) {
        viewModelScope.launch {
            repository.create(CreateTargetRequest(categoryId, amount)).onSuccess {
                currentPeriodId?.let { load(it) }
            }
        }
    }

    fun updateTarget(id: String, amount: Long) {
        viewModelScope.launch {
            repository.update(id, UpdateTargetRequest(amount)).onSuccess {
                currentPeriodId?.let { load(it) }
            }
        }
    }
}
