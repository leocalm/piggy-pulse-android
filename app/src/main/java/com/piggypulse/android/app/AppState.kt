package com.piggypulse.android.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piggypulse.android.core.model.BudgetPeriod
import com.piggypulse.android.core.model.LoginRequest
import com.piggypulse.android.core.model.LoginResponse
import com.piggypulse.android.core.model.RegisterRequest
import com.piggypulse.android.core.model.TwoFactorRequest
import com.piggypulse.android.core.model.User
import com.piggypulse.android.core.network.ApiClient
import com.piggypulse.android.core.network.ApiError
import com.piggypulse.android.core.network.TokenManager
import com.piggypulse.android.core.repository.PeriodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppState @Inject constructor(
    private val apiClient: ApiClient,
    private val tokenManager: TokenManager,
    private val periodRepository: PeriodRepository,
) : ViewModel() {

    val isAuthenticated: StateFlow<Boolean> = tokenManager.isAuthenticated
        .stateIn(viewModelScope, SharingStarted.Eagerly, tokenManager.getAccessToken() != null)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _selectedPeriodId = MutableStateFlow<String?>(null)
    val selectedPeriodId: StateFlow<String?> = _selectedPeriodId.asStateFlow()

    private val _periods = MutableStateFlow<List<BudgetPeriod>>(emptyList())
    val periods: StateFlow<List<BudgetPeriod>> = _periods.asStateFlow()

    private val _isInitializing = MutableStateFlow(true)
    val isInitializing: StateFlow<Boolean> = _isInitializing.asStateFlow()

    init {
        if (tokenManager.getAccessToken() != null) {
            validateSession()
        } else {
            _isInitializing.value = false
        }
    }

    private fun validateSession() {
        viewModelScope.launch {
            val result = apiClient.request { apiClient.service.getMe() }
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    loadPeriods()
                },
                onFailure = { error ->
                    if (error is ApiError.Unauthorized) {
                        tokenManager.clearTokens()
                    }
                },
            )
            _isInitializing.value = false
        }
    }

    fun loadPeriods() {
        viewModelScope.launch {
            periodRepository.fetchPeriods().onSuccess { periods ->
                _periods.value = periods
                if (_selectedPeriodId.value == null) {
                    val active = periods.firstOrNull { it.status == "Active" }
                    _selectedPeriodId.value = active?.id ?: periods.firstOrNull()?.id
                }
            }
        }
    }

    fun selectPeriod(periodId: String?) {
        _selectedPeriodId.value = periodId
    }

    val selectedPeriod: BudgetPeriod?
        get() = _periods.value.firstOrNull { it.id == _selectedPeriodId.value }

    suspend fun login(email: String, password: String): LoginResult {
        val result = apiClient.request {
            apiClient.service.login(
                request = LoginRequest(
                    email = email.trim().lowercase(),
                    password = password,
                ),
            )
        }

        return result.fold(
            onSuccess = { response ->
                handleLoginResponse(response)
            },
            onFailure = { error ->
                when (error) {
                    is ApiError.TwoFactorRequired ->
                        LoginResult.TwoFactorRequired(error.twoFactorToken)
                    is ApiError.Validation ->
                        LoginResult.Error(error.message)
                    else ->
                        LoginResult.Error("Login failed. Please check your credentials.")
                }
            },
        )
    }

    suspend fun register(name: String, email: String, password: String): LoginResult {
        val result = apiClient.request {
            apiClient.service.register(
                request = RegisterRequest(
                    name = name.trim(),
                    email = email.trim().lowercase(),
                    password = password,
                ),
            )
        }

        return result.fold(
            onSuccess = { response -> handleLoginResponse(response) },
            onFailure = { error ->
                when (error) {
                    is ApiError.Validation -> LoginResult.Error(error.message)
                    else -> LoginResult.Error("Registration failed. Please try again.")
                }
            },
        )
    }

    suspend fun verifyTwoFactor(twoFactorToken: String, code: String): LoginResult {
        val result = apiClient.request {
            apiClient.service.verifyTwoFactor(
                request = TwoFactorRequest(
                    twoFactorToken = twoFactorToken,
                    code = code,
                ),
            )
        }

        return result.fold(
            onSuccess = { response -> handleLoginResponse(response) },
            onFailure = { error ->
                when (error) {
                    is ApiError.Validation -> LoginResult.Error(error.message)
                    else -> LoginResult.Error("Invalid code. Please try again.")
                }
            },
        )
    }

    fun logout() {
        viewModelScope.launch {
            apiClient.requestUnit { apiClient.service.logout() }
            tokenManager.clearTokens()
            _currentUser.value = null
            _selectedPeriodId.value = null
            _periods.value = emptyList()
        }
    }

    private fun handleLoginResponse(response: LoginResponse): LoginResult {
        if (response.requiresTwoFactor && response.twoFactorToken != null) {
            return LoginResult.TwoFactorRequired(response.twoFactorToken)
        }

        val token = response.token ?: return LoginResult.Error("No token received")
        tokenManager.setTokens(token)
        _currentUser.value = response.user
        loadPeriods()

        return LoginResult.Success
    }
}

sealed interface LoginResult {
    data object Success : LoginResult
    data class TwoFactorRequired(val token: String) : LoginResult
    data class Error(val message: String) : LoginResult
}
