package com.piggypulse.android.feature.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piggypulse.android.core.model.CreateTransactionRequest
import com.piggypulse.android.core.model.Transaction
import com.piggypulse.android.core.model.TransactionDirection
import com.piggypulse.android.core.model.TransactionFilterOptions
import com.piggypulse.android.core.model.UpdateTransactionRequest
import com.piggypulse.android.core.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: TransactionRepository,
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedDirection = MutableStateFlow(TransactionDirection.All)
    val selectedDirection: StateFlow<TransactionDirection> = _selectedDirection.asStateFlow()

    private val _selectedAccountIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedAccountIds: StateFlow<Set<String>> = _selectedAccountIds.asStateFlow()

    private val _selectedCategoryIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedCategoryIds: StateFlow<Set<String>> = _selectedCategoryIds.asStateFlow()

    private val _selectedVendorIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedVendorIds: StateFlow<Set<String>> = _selectedVendorIds.asStateFlow()

    private val _filterOptions = MutableStateFlow(TransactionFilterOptions())
    val filterOptions: StateFlow<TransactionFilterOptions> = _filterOptions.asStateFlow()

    private val _editingTransaction = MutableStateFlow<Transaction?>(null)
    val editingTransaction: StateFlow<Transaction?> = _editingTransaction.asStateFlow()

    private val _showForm = MutableStateFlow(false)
    val showForm: StateFlow<Boolean> = _showForm.asStateFlow()

    // Exposed as StateFlow so the badge count updates reactively in the UI.
    val activeFilterCount: StateFlow<Int> = combine(
        _selectedAccountIds,
        _selectedCategoryIds,
        _selectedVendorIds,
    ) { accounts, categories, vendors ->
        accounts.size + categories.size + vendors.size
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    private var nextCursor: String? = null
    private var currentPeriodId: String? = null

    // Tracks the current first-page load job so rapid re-loads cancel the prior in-flight request.
    private var loadJob: Job? = null

    val hasMore: Boolean get() = nextCursor != null

    fun load(periodId: String) {
        currentPeriodId = periodId

        // Cancel any in-flight first-page load to avoid stale results racing back.
        loadJob?.cancel()

        nextCursor = null
        _isLoading.value = true
        _errorMessage.value = null

        loadJob = viewModelScope.launch {
            repository.fetchTransactions(
                periodId = periodId,
                direction = _selectedDirection.value.queryValue,
                accountIds = _selectedAccountIds.value.toList(),
                categoryIds = _selectedCategoryIds.value.toList(),
                vendorIds = _selectedVendorIds.value.toList(),
            ).fold(
                onSuccess = { result ->
                    _transactions.value = result.data
                    nextCursor = result.nextCursor
                },
                onFailure = { _errorMessage.value = "Failed to load transactions" },
            )
            _isLoading.value = false
        }
    }

    fun loadMore() {
        val periodId = currentPeriodId ?: return
        val cursor = nextCursor ?: return
        if (_isLoadingMore.value) return

        _isLoadingMore.value = true
        viewModelScope.launch {
            repository.fetchTransactions(
                periodId = periodId,
                cursor = cursor,
                direction = _selectedDirection.value.queryValue,
                accountIds = _selectedAccountIds.value.toList(),
                categoryIds = _selectedCategoryIds.value.toList(),
                vendorIds = _selectedVendorIds.value.toList(),
            ).fold(
                onSuccess = { result ->
                    _transactions.value = _transactions.value + result.data
                    nextCursor = result.nextCursor
                },
                onFailure = { },
            )
            _isLoadingMore.value = false
        }
    }

    fun setDirection(direction: TransactionDirection) {
        _selectedDirection.value = direction
        currentPeriodId?.let { load(it) }
    }

    fun applyFilters(accountIds: Set<String>, categoryIds: Set<String>, vendorIds: Set<String>) {
        _selectedAccountIds.value = accountIds
        _selectedCategoryIds.value = categoryIds
        _selectedVendorIds.value = vendorIds
        currentPeriodId?.let { load(it) }
    }

    fun clearFilters() {
        _selectedAccountIds.value = emptySet()
        _selectedCategoryIds.value = emptySet()
        _selectedVendorIds.value = emptySet()
        currentPeriodId?.let { load(it) }
    }

    fun loadFilterOptions() {
        viewModelScope.launch {
            repository.fetchFilterOptions().onSuccess {
                _filterOptions.value = it
            }
        }
    }

    fun openCreateForm() {
        _editingTransaction.value = null
        _showForm.value = true
    }

    fun openEditForm(transaction: Transaction) {
        _editingTransaction.value = transaction
        _showForm.value = true
    }

    fun closeForm() {
        _showForm.value = false
        _editingTransaction.value = null
    }

    fun createTransaction(request: CreateTransactionRequest) {
        viewModelScope.launch {
            repository.createTransaction(request).onSuccess {
                closeForm()
                currentPeriodId?.let { load(it) }
            }
        }
    }

    fun updateTransaction(id: String, request: UpdateTransactionRequest) {
        viewModelScope.launch {
            repository.updateTransaction(id, request).onSuccess {
                closeForm()
                currentPeriodId?.let { load(it) }
            }
        }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            repository.deleteTransaction(id).onSuccess {
                currentPeriodId?.let { load(it) }
            }
        }
    }
}
