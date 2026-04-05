package com.piggypulse.android.feature.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piggypulse.android.core.model.CategoryListItem
import com.piggypulse.android.core.model.CreateCategoryRequest
import com.piggypulse.android.core.model.UpdateCategoryRequest
import com.piggypulse.android.core.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val repository: CategoryRepository,
) : ViewModel() {

    private val _categories = MutableStateFlow<List<CategoryListItem>>(emptyList())
    val categories: StateFlow<List<CategoryListItem>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _showForm = MutableStateFlow(false)
    val showForm: StateFlow<Boolean> = _showForm.asStateFlow()

    private val _editingCategory = MutableStateFlow<CategoryListItem?>(null)
    val editingCategory: StateFlow<CategoryListItem?> = _editingCategory.asStateFlow()

    private val _selectedTab = MutableStateFlow("expense")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    private var loadJob: Job? = null

    val filteredCategories: List<CategoryListItem>
        get() = _categories.value.filter {
            it.type == _selectedTab.value && it.status.equals("active", ignoreCase = true)
        }

    fun load() {
        loadJob?.cancel()
        _isLoading.value = true
        loadJob = viewModelScope.launch {
            repository.fetchAll().fold(
                onSuccess = { _categories.value = it },
                onFailure = { },
            )
            _isLoading.value = false
        }
    }

    fun setTab(type: String) { _selectedTab.value = type }

    fun openCreateForm() { _editingCategory.value = null; _showForm.value = true }
    fun openEditForm(c: CategoryListItem) { _editingCategory.value = c; _showForm.value = true }
    fun closeForm() { _showForm.value = false; _editingCategory.value = null }

    fun create(request: CreateCategoryRequest) {
        viewModelScope.launch {
            repository.create(request).onSuccess { closeForm(); load() }
        }
    }

    fun update(id: String, request: UpdateCategoryRequest) {
        viewModelScope.launch {
            repository.update(id, request).onSuccess { closeForm(); load() }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch { repository.delete(id).onSuccess { load() } }
    }

    fun archive(id: String) {
        viewModelScope.launch { repository.archive(id).onSuccess { load() } }
    }
}
