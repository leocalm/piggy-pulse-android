package com.piggypulse.android.feature.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piggypulse.android.core.model.AccountDetails
import com.piggypulse.android.core.model.AccountSummary
import com.piggypulse.android.core.model.AccountType
import com.piggypulse.android.core.model.CreateAccountRequest
import com.piggypulse.android.core.model.UpdateAccountRequest
import com.piggypulse.android.core.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val repository: AccountRepository,
) : ViewModel() {

    private val _accounts = MutableStateFlow<List<AccountSummary>>(emptyList())
    val accounts: StateFlow<List<AccountSummary>> = _accounts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _showForm = MutableStateFlow(false)
    val showForm: StateFlow<Boolean> = _showForm.asStateFlow()

    private val _editingAccount = MutableStateFlow<AccountSummary?>(null)
    val editingAccount: StateFlow<AccountSummary?> = _editingAccount.asStateFlow()

    // Account detail
    private val _accountDetails = MutableStateFlow<AccountDetails?>(null)
    val accountDetails: StateFlow<AccountDetails?> = _accountDetails.asStateFlow()

    private val _isLoadingDetails = MutableStateFlow(false)
    val isLoadingDetails: StateFlow<Boolean> = _isLoadingDetails.asStateFlow()

    private var loadJob: Job? = null

    val groupedAccounts: Map<String, List<AccountSummary>>
        get() = _accounts.value
            .filter { it.status.equals("active", ignoreCase = true) }
            .groupBy { it.type }

    val netPosition: Long
        get() {
            val active = _accounts.value.filter { it.status.equals("active", ignoreCase = true) }
            return active.sumOf { account ->
                if (account.type == "credit_card") -account.currentBalance
                else account.currentBalance
            }
        }

    fun load() {
        loadJob?.cancel()
        _isLoading.value = true
        _errorMessage.value = null
        loadJob = viewModelScope.launch {
            repository.fetchSummaries().fold(
                onSuccess = { _accounts.value = it },
                onFailure = { _errorMessage.value = "Failed to load accounts" },
            )
            _isLoading.value = false
        }
    }

    fun loadDetails(accountId: String) {
        _accountDetails.value = null
        _isLoadingDetails.value = true
        viewModelScope.launch {
            repository.fetchDetails(accountId).fold(
                onSuccess = { _accountDetails.value = it },
                onFailure = { _errorMessage.value = "Failed to load account details" },
            )
            _isLoadingDetails.value = false
        }
    }

    fun openCreateForm() {
        _editingAccount.value = null
        _showForm.value = true
    }

    fun openEditForm(account: AccountSummary) {
        _editingAccount.value = account
        _showForm.value = true
    }

    fun closeForm() {
        _showForm.value = false
        _editingAccount.value = null
    }

    fun createAccount(request: CreateAccountRequest) {
        viewModelScope.launch {
            repository.create(request).onSuccess {
                closeForm()
                load()
            }
        }
    }

    fun updateAccount(id: String, request: UpdateAccountRequest) {
        viewModelScope.launch {
            repository.update(id, request).onSuccess {
                closeForm()
                load()
            }
        }
    }

    fun deleteAccount(id: String) {
        viewModelScope.launch {
            repository.delete(id).onSuccess { load() }
        }
    }

    fun archiveAccount(id: String) {
        viewModelScope.launch {
            repository.archive(id).onSuccess { load() }
        }
    }

    fun unarchiveAccount(id: String) {
        viewModelScope.launch {
            repository.unarchive(id).onSuccess { load() }
        }
    }
}
