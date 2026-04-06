package com.piggypulse.android.feature.periods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piggypulse.android.core.model.BudgetPeriod
import com.piggypulse.android.core.model.CreatePeriodRequest
import com.piggypulse.android.core.model.CreateScheduleRequest
import com.piggypulse.android.core.model.PeriodScheduleResponse
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

    private val _showScheduleForm = MutableStateFlow(false)
    val showScheduleForm: StateFlow<Boolean> = _showScheduleForm.asStateFlow()

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
    fun closeForm() { _showCreateForm.value = false }

    fun openScheduleForm() { _showScheduleForm.value = true }
    fun closeScheduleForm() { _showScheduleForm.value = false }

    fun createManualEndDate(name: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            repository.create(
                CreatePeriodRequest(
                    name = name,
                    startDate = startDate,
                    periodType = "manualEndDate",
                    manualEndDate = endDate,
                ),
            ).onSuccess { closeForm(); load() }
        }
    }

    fun saveSchedule(request: CreateScheduleRequest) {
        viewModelScope.launch {
            val isNew = _schedule.value == null || _schedule.value?.scheduleType != "automatic"
            val result = if (isNew) {
                repository.createSchedule(request)
            } else {
                repository.updateSchedule(request)
            }
            result.onSuccess {
                _schedule.value = it
                closeScheduleForm()
                load()
            }
        }
    }

    fun deleteSchedule() {
        viewModelScope.launch {
            repository.deleteSchedule().onSuccess {
                _schedule.value = null
                closeScheduleForm()
                load()
            }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            repository.delete(id).onSuccess { load() }
        }
    }
}
