package com.piggypulse.android.core.network

import com.piggypulse.android.core.model.AccountOptionList
import com.piggypulse.android.core.model.CategoryOptionList
import com.piggypulse.android.core.model.ChangePasswordRequest
import com.piggypulse.android.core.model.CreateTransactionRequest
import com.piggypulse.android.core.model.ForgotPasswordRequest
import com.piggypulse.android.core.model.ForgotPasswordResponse
import com.piggypulse.android.core.model.LoginRequest
import com.piggypulse.android.core.model.LoginResponse
import com.piggypulse.android.core.model.PaginatedPeriods
import com.piggypulse.android.core.model.PaginatedTransactions
import com.piggypulse.android.core.model.RegisterRequest
import com.piggypulse.android.core.model.Transaction
import com.piggypulse.android.core.model.TwoFactorRequest
import com.piggypulse.android.core.model.UpdateTransactionRequest
import com.piggypulse.android.core.model.User
import com.piggypulse.android.core.model.VendorOptionList
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Auth — no auth required
    @POST("auth/login")
    suspend fun login(
        @Header(AuthInterceptor.HEADER_NO_AUTH) noAuth: String = "true",
        @Body request: LoginRequest,
    ): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(
        @Header(AuthInterceptor.HEADER_NO_AUTH) noAuth: String = "true",
        @Body request: RegisterRequest,
    ): Response<LoginResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(
        @Header(AuthInterceptor.HEADER_NO_AUTH) noAuth: String = "true",
        @Body request: ForgotPasswordRequest,
    ): Response<ForgotPasswordResponse>

    @POST("auth/2fa/verify")
    suspend fun verifyTwoFactor(
        @Header(AuthInterceptor.HEADER_NO_AUTH) noAuth: String = "true",
        @Body request: TwoFactorRequest,
    ): Response<LoginResponse>

    // Auth — requires auth
    @GET("auth/me")
    suspend fun getMe(): Response<User>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    @POST("auth/password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest,
    ): Response<Unit>

    // Periods
    @GET("periods")
    suspend fun getPeriods(
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int? = null,
    ): Response<PaginatedPeriods>

    // Transactions
    @GET("transactions")
    suspend fun getTransactions(
        @Query("periodId") periodId: String,
        @Query("limit") limit: Int? = 20,
        @Query("cursor") cursor: String? = null,
        @Query("direction") direction: String? = null,
        @Query("accountId") accountIds: List<String>? = null,
        @Query("categoryId") categoryIds: List<String>? = null,
        @Query("vendorId") vendorIds: List<String>? = null,
    ): Response<PaginatedTransactions>

    @POST("transactions")
    suspend fun createTransaction(
        @Body request: CreateTransactionRequest,
    ): Response<Transaction>

    @PUT("transactions/{id}")
    suspend fun updateTransaction(
        @Path("id") id: String,
        @Body request: UpdateTransactionRequest,
    ): Response<Transaction>

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(
        @Path("id") id: String,
    ): Response<Unit>

    // Filter options
    @GET("accounts/options")
    suspend fun getAccountOptions(): Response<AccountOptionList>

    @GET("categories/options")
    suspend fun getCategoryOptions(): Response<CategoryOptionList>

    @GET("vendors/options")
    suspend fun getVendorOptions(): Response<VendorOptionList>
}
