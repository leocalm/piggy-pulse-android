package com.piggypulse.android.feature.overlays

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piggypulse.android.core.model.CreateOverlayRequest
import com.piggypulse.android.core.model.OverlayItem
import com.piggypulse.android.core.model.UpdateOverlayRequest
import com.piggypulse.android.core.repository.OverlayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverlaysViewModel @Inject constructor(
    private val repository: OverlayRepository,
) : ViewModel() {

    private val _overlays = MutableStateFlow<List<OverlayItem>>(emptyList())
    val overlays: StateFlow<List<OverlayItem>> = _overlays.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _showForm = MutableStateFlow(false)
    val showForm: StateFlow<Boolean> = _showForm.asStateFlow()

    private val _editingOverlay = MutableStateFlow<OverlayItem?>(null)
    val editingOverlay: StateFlow<OverlayItem?> = _editingOverlay.asStateFlow()

    private var loadJob: Job? = null

    fun load() {
        loadJob?.cancel()
        _isLoading.value = true
        loadJob = viewModelScope.launch {
            repository.fetchAll().onSuccess { _overlays.value = it }
            _isLoading.value = false
        }
    }

    fun openCreateForm() { _editingOverlay.value = null; _showForm.value = true }
    fun openEditForm(o: OverlayItem) { _editingOverlay.value = o; _showForm.value = true }
    fun closeForm() { _showForm.value = false; _editingOverlay.value = null }

    fun create(request: CreateOverlayRequest) {
        viewModelScope.launch {
            repository.create(request).onSuccess { closeForm(); load() }
        }
    }

    fun update(id: String, request: UpdateOverlayRequest) {
        viewModelScope.launch {
            repository.update(id, request).onSuccess { closeForm(); load() }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch { repository.delete(id).onSuccess { load() } }
    }
}
