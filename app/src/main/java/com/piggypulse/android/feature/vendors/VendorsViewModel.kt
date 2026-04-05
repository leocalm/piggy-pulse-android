package com.piggypulse.android.feature.vendors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piggypulse.android.core.model.CreateVendorRequest
import com.piggypulse.android.core.model.UpdateVendorRequest
import com.piggypulse.android.core.model.VendorSummary
import com.piggypulse.android.core.repository.VendorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VendorsViewModel @Inject constructor(
    private val repository: VendorRepository,
) : ViewModel() {

    private val _vendors = MutableStateFlow<List<VendorSummary>>(emptyList())
    val vendors: StateFlow<List<VendorSummary>> = _vendors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _showForm = MutableStateFlow(false)
    val showForm: StateFlow<Boolean> = _showForm.asStateFlow()

    private val _editingVendor = MutableStateFlow<VendorSummary?>(null)
    val editingVendor: StateFlow<VendorSummary?> = _editingVendor.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var currentPeriodId: String? = null
    private var loadJob: Job? = null

    val filteredVendors: List<VendorSummary>
        get() {
            val query = _searchQuery.value.lowercase()
            return if (query.isBlank()) _vendors.value
            else _vendors.value.filter { it.name.lowercase().contains(query) }
        }

    fun load(periodId: String?) {
        currentPeriodId = periodId
        loadJob?.cancel()
        _isLoading.value = true
        loadJob = viewModelScope.launch {
            repository.fetchAll(periodId).fold(
                onSuccess = { _vendors.value = it },
                onFailure = { },
            )
            _isLoading.value = false
        }
    }

    fun setSearch(query: String) { _searchQuery.value = query }

    fun openCreateForm() { _editingVendor.value = null; _showForm.value = true }
    fun openEditForm(v: VendorSummary) { _editingVendor.value = v; _showForm.value = true }
    fun closeForm() { _showForm.value = false; _editingVendor.value = null }

    fun create(request: CreateVendorRequest) {
        viewModelScope.launch {
            repository.create(request).onSuccess { closeForm(); load(currentPeriodId) }
        }
    }

    fun update(id: String, request: UpdateVendorRequest) {
        viewModelScope.launch {
            repository.update(id, request).onSuccess { closeForm(); load(currentPeriodId) }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch { repository.delete(id).onSuccess { load(currentPeriodId) } }
    }

    fun merge(sourceId: String, targetId: String) {
        viewModelScope.launch {
            repository.merge(sourceId, targetId).onSuccess { load(currentPeriodId) }
        }
    }
}
