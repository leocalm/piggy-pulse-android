package com.piggypulse.android.feature.periods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piggypulse.android.core.model.BudgetPeriod
import com.piggypulse.android.core.model.CreatePeriodRequest
import com.piggypulse.android.core.model.PeriodDuration
import com.piggypulse.android.core.model.PeriodScheduleResponse
import com.piggypulse.android.core.model.UpdatePeriodRequest
import com.piggypulse.android.core.repository.PeriodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PeriodsViewModel @Inject constructor(
    private val repository: PeriodRepository,
) : ViewModel() {

    private val _periods = MutableStateFlow<List<BudgetPeriod>>(emptyList())
    val periods: StateFlow<List<BudgetPeriod>> = _periods.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _schedule = MutableStateFlow<PeriodScheduleResponse?>(null)
    val schedule: StateFlow<PeriodScheduleResponse?> = _schedule.asStateFlow()

    private val _showCreateForm = MutableStateFlow(false)
    val showCreateForm: StateFlow<Boolean> = _showCreateForm.asStateFlow()

    private val _editingPeriod = MutableStateFlow<BudgetPeriod?>(null)
    val editingPeriod: StateFlow<BudgetPeriod?> = _editingPeriod.asStateFlow()

    private var loadJob: Job? = null

    fun load() {
        loadJob?.cancel()
        _isLoading.value = true
        loadJob = viewModelScope.launch {
            repository.fetchPeriods().onSuccess { _periods.value = it }
            repository.fetchSchedule().onSuccess { _schedule.value = it }
            _isLoading.value = false
        }
    }

    fun openCreateForm() { _showCreateForm.value = true }

    fun openEditForm(period: BudgetPeriod) { _editingPeriod.value = period }

    fun closeForm() {
        _showCreateForm.value = false
        _editingPeriod.value = null
    }

    fun create(name: String, startDate: String, durationUnits: Int, durationUnit: String) {
        viewModelScope.launch {
            repository.create(
                CreatePeriodRequest(
                    name = name,
                    startDate = startDate,
                    duration = PeriodDuration(durationUnits, durationUnit),
                ),
            ).onSuccess { closeForm(); load() }
        }
    }

    fun update(id: String, name: String, startDate: String, durationUnits: Int, durationUnit: String) {
        viewModelScope.launch {
            repository.update(
                id,
                UpdatePeriodRequest(
                    name = name,
                    startDate = startDate,
                    duration = PeriodDuration(durationUnits, durationUnit),
                ),
            ).onSuccess { closeForm(); load() }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            repository.delete(id).onSuccess { load() }
        }
    }
}
