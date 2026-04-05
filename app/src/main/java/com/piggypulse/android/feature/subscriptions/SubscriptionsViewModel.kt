package com.piggypulse.android.feature.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piggypulse.android.core.model.CreateSubscriptionRequest
import com.piggypulse.android.core.model.SubscriptionItem
import com.piggypulse.android.core.model.UpcomingCharge
import com.piggypulse.android.core.model.UpdateSubscriptionRequest
import com.piggypulse.android.core.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val repository: SubscriptionRepository,
) : ViewModel() {

    private val _subscriptions = MutableStateFlow<List<SubscriptionItem>>(emptyList())
    val subscriptions: StateFlow<List<SubscriptionItem>> = _subscriptions.asStateFlow()

    private val _upcoming = MutableStateFlow<List<UpcomingCharge>>(emptyList())
    val upcoming: StateFlow<List<UpcomingCharge>> = _upcoming.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _showForm = MutableStateFlow(false)
    val showForm: StateFlow<Boolean> = _showForm.asStateFlow()

    private val _editingSubscription = MutableStateFlow<SubscriptionItem?>(null)
    val editingSubscription: StateFlow<SubscriptionItem?> = _editingSubscription.asStateFlow()

    private val _selectedTab = MutableStateFlow("active")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    private var currentPeriodId: String? = null
    private var loadJob: Job? = null

    val filteredSubscriptions: List<SubscriptionItem>
        get() = _subscriptions.value.filter {
            it.status.equals(_selectedTab.value, ignoreCase = true)
        }

    fun load(periodId: String?) {
        currentPeriodId = periodId
        loadJob?.cancel()
        _isLoading.value = true
        loadJob = viewModelScope.launch {
            repository.fetchAll(periodId).onSuccess { _subscriptions.value = it }
            repository.fetchUpcoming(periodId).onSuccess { _upcoming.value = it }
            _isLoading.value = false
        }
    }

    fun setTab(status: String) { _selectedTab.value = status }

    fun openCreateForm() { _editingSubscription.value = null; _showForm.value = true }
    fun openEditForm(s: SubscriptionItem) { _editingSubscription.value = s; _showForm.value = true }
    fun closeForm() { _showForm.value = false; _editingSubscription.value = null }

    fun create(request: CreateSubscriptionRequest) {
        viewModelScope.launch {
            repository.create(request).onSuccess { closeForm(); load(currentPeriodId) }
        }
    }

    fun update(id: String, request: UpdateSubscriptionRequest) {
        viewModelScope.launch {
            repository.update(id, request).onSuccess { closeForm(); load(currentPeriodId) }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch { repository.delete(id).onSuccess { load(currentPeriodId) } }
    }

    fun cancel(id: String, date: String?) {
        viewModelScope.launch { repository.cancel(id, date).onSuccess { load(currentPeriodId) } }
    }
}
