package com.piggypulse.android.core.network

import com.piggypulse.android.core.model.AccountDetails
import com.piggypulse.android.core.model.AccountOption
import com.piggypulse.android.core.model.AccountResponse
import com.piggypulse.android.core.model.CancelSubscriptionRequest
import com.piggypulse.android.core.model.CategoryDetail
import com.piggypulse.android.core.model.CategoryOption
import com.piggypulse.android.core.model.CategoryOverviewResponse
import com.piggypulse.android.core.model.CategoryResponse
import com.piggypulse.android.core.model.ChangePasswordRequest
import com.piggypulse.android.core.model.CreateAccountRequest
import com.piggypulse.android.core.model.CreatePeriodRequest
import com.piggypulse.android.core.model.CreateScheduleRequest
import com.piggypulse.android.core.model.CreateTargetRequest
import com.piggypulse.android.core.model.DashboardCashFlow
import com.piggypulse.android.core.model.DashboardCurrentPeriod
import com.piggypulse.android.core.model.DashboardFixedCategoryItem
import com.piggypulse.android.core.model.DashboardNetPosition
import com.piggypulse.android.core.model.DashboardSpendingTrend
import com.piggypulse.android.core.model.DashboardSubscriptions
import com.piggypulse.android.core.model.DashboardTopVendorItem
import com.piggypulse.android.core.model.CreateCategoryRequest
import com.piggypulse.android.core.model.CreateOverlayRequest
import com.piggypulse.android.core.model.CreateSubscriptionRequest
import com.piggypulse.android.core.model.CreateVendorRequest
import com.piggypulse.android.core.model.MergeVendorRequest
import com.piggypulse.android.core.model.OnboardingCompleteResponse
import com.piggypulse.android.core.model.OnboardingStatusResponse
import com.piggypulse.android.core.model.OverlayResponse
import com.piggypulse.android.core.model.PaginatedOverlays
import com.piggypulse.android.core.model.PaginatedAccountSummaries
import com.piggypulse.android.core.model.CategoryTargetsResponse
import com.piggypulse.android.core.model.PaginatedCategories
import com.piggypulse.android.core.model.PeriodResponse
import com.piggypulse.android.core.model.PeriodScheduleResponse
import com.piggypulse.android.core.model.PreferencesResponse
import com.piggypulse.android.core.model.ProfileResponse
import com.piggypulse.android.core.model.PaginatedVendors
import com.piggypulse.android.core.model.SubscriptionDetailResponse
import com.piggypulse.android.core.model.SubscriptionItem
import com.piggypulse.android.core.model.SubscriptionResponse
import com.piggypulse.android.core.model.UpcomingCharge
import com.piggypulse.android.core.model.UpdateAccountRequest
import com.piggypulse.android.core.model.TargetResponse
import com.piggypulse.android.core.model.UpdateCategoryRequest
import com.piggypulse.android.core.model.UpdateOverlayRequest
import com.piggypulse.android.core.model.UpdatePeriodRequest
import com.piggypulse.android.core.model.UpdatePreferencesRequest
import com.piggypulse.android.core.model.UpdateProfileRequest
import com.piggypulse.android.core.model.UpdateTargetRequest
import com.piggypulse.android.core.model.UpdateSubscriptionRequest
import com.piggypulse.android.core.model.UpdateVendorRequest
import com.piggypulse.android.core.model.VendorDetail
import com.piggypulse.android.core.model.VendorResponse
import com.piggypulse.android.core.model.VendorStatsResponse
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
import com.piggypulse.android.core.model.VendorOption
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
    suspend fun getAccountOptions(): Response<List<AccountOption>>

    @GET("categories/options")
    suspend fun getCategoryOptions(): Response<List<CategoryOption>>

    @GET("vendors/options")
    suspend fun getVendorOptions(): Response<List<VendorOption>>

    // Accounts
    @GET("accounts/summary")
    suspend fun getAccountSummaries(
        @Query("periodId") periodId: String? = null,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int? = null,
    ): Response<PaginatedAccountSummaries>

    @GET("accounts/{id}/details")
    suspend fun getAccountDetails(
        @Path("id") id: String,
    ): Response<AccountDetails>

    @POST("accounts")
    suspend fun createAccount(
        @Body request: CreateAccountRequest,
    ): Response<AccountResponse>

    @PUT("accounts/{id}")
    suspend fun updateAccount(
        @Path("id") id: String,
        @Body request: UpdateAccountRequest,
    ): Response<AccountResponse>

    @DELETE("accounts/{id}")
    suspend fun deleteAccount(
        @Path("id") id: String,
    ): Response<Unit>

    @POST("accounts/{id}/archive")
    suspend fun archiveAccount(
        @Path("id") id: String,
    ): Response<AccountResponse>

    @POST("accounts/{id}/unarchive")
    suspend fun unarchiveAccount(
        @Path("id") id: String,
    ): Response<AccountResponse>

    // Categories
    @GET("categories")
    suspend fun getCategories(
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int? = null,
    ): Response<PaginatedCategories>

    @GET("categories/overview")
    suspend fun getCategoriesOverview(
        @Query("periodId") periodId: String,
    ): Response<CategoryOverviewResponse>

    @GET("categories/{id}")
    suspend fun getCategoryDetail(
        @Path("id") id: String,
        @Query("periodId") periodId: String? = null,
    ): Response<CategoryDetail>

    @POST("categories")
    suspend fun createCategory(
        @Body request: CreateCategoryRequest,
    ): Response<CategoryResponse>

    @PUT("categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: String,
        @Body request: UpdateCategoryRequest,
    ): Response<CategoryResponse>

    @DELETE("categories/{id}")
    suspend fun deleteCategory(
        @Path("id") id: String,
    ): Response<Unit>

    @POST("categories/{id}/archive")
    suspend fun archiveCategory(
        @Path("id") id: String,
    ): Response<CategoryResponse>

    @POST("categories/{id}/unarchive")
    suspend fun unarchiveCategory(
        @Path("id") id: String,
    ): Response<CategoryResponse>

    // Vendors
    @GET("vendors")
    suspend fun getVendors(
        @Query("periodId") periodId: String? = null,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int? = null,
    ): Response<PaginatedVendors>

    @GET("vendors/stats")
    suspend fun getVendorStats(
        @Query("periodId") periodId: String? = null,
    ): Response<VendorStatsResponse>

    @GET("vendors/{id}")
    suspend fun getVendorDetail(
        @Path("id") id: String,
        @Query("periodId") periodId: String? = null,
    ): Response<VendorDetail>

    @POST("vendors")
    suspend fun createVendor(
        @Body request: CreateVendorRequest,
    ): Response<VendorResponse>

    @PUT("vendors/{id}")
    suspend fun updateVendor(
        @Path("id") id: String,
        @Body request: UpdateVendorRequest,
    ): Response<VendorResponse>

    @DELETE("vendors/{id}")
    suspend fun deleteVendor(
        @Path("id") id: String,
    ): Response<Unit>

    @POST("vendors/{id}/merge")
    suspend fun mergeVendor(
        @Path("id") sourceId: String,
        @Body request: MergeVendorRequest,
    ): Response<VendorResponse>

    // Subscriptions
    @GET("subscriptions")
    suspend fun getSubscriptions(
        @Query("periodId") periodId: String? = null,
        @Query("categoryId") categoryId: String? = null,
    ): Response<List<SubscriptionItem>>

    @GET("subscriptions/upcoming")
    suspend fun getUpcomingCharges(
        @Query("periodId") periodId: String? = null,
        @Query("limit") limit: Int? = null,
    ): Response<List<UpcomingCharge>>

    @GET("subscriptions/{id}")
    suspend fun getSubscriptionDetail(
        @Path("id") id: String,
    ): Response<SubscriptionDetailResponse>

    @POST("subscriptions")
    suspend fun createSubscription(
        @Body request: CreateSubscriptionRequest,
    ): Response<SubscriptionResponse>

    @PUT("subscriptions/{id}")
    suspend fun updateSubscription(
        @Path("id") id: String,
        @Body request: UpdateSubscriptionRequest,
    ): Response<SubscriptionResponse>

    @DELETE("subscriptions/{id}")
    suspend fun deleteSubscription(
        @Path("id") id: String,
    ): Response<Unit>

    @POST("subscriptions/{id}/cancel")
    suspend fun cancelSubscription(
        @Path("id") id: String,
        @Body request: CancelSubscriptionRequest,
    ): Response<SubscriptionResponse>

    // Dashboard
    @GET("dashboard/current-period")
    suspend fun getDashboardCurrentPeriod(
        @Query("periodId") periodId: String,
    ): Response<DashboardCurrentPeriod>

    @GET("dashboard/net-position")
    suspend fun getDashboardNetPosition(
        @Query("periodId") periodId: String,
    ): Response<DashboardNetPosition>

    @GET("dashboard/cash-flow")
    suspend fun getDashboardCashFlow(
        @Query("periodId") periodId: String,
    ): Response<DashboardCashFlow>

    @GET("dashboard/spending-trend")
    suspend fun getDashboardSpendingTrend(
        @Query("periodId") periodId: String,
    ): Response<DashboardSpendingTrend>

    @GET("dashboard/top-vendors")
    suspend fun getDashboardTopVendors(
        @Query("periodId") periodId: String,
        @Query("limit") limit: Int? = 5,
    ): Response<List<DashboardTopVendorItem>>

    @GET("dashboard/subscriptions")
    suspend fun getDashboardSubscriptions(
        @Query("periodId") periodId: String,
    ): Response<DashboardSubscriptions>

    @GET("dashboard/fixed-categories")
    suspend fun getDashboardFixedCategories(
        @Query("periodId") periodId: String,
    ): Response<List<DashboardFixedCategoryItem>>

    @GET("transactions")
    suspend fun getRecentTransactions(
        @Query("periodId") periodId: String,
        @Query("limit") limit: Int? = 7,
    ): Response<PaginatedTransactions>

    // Periods — additional endpoints
    @POST("periods")
    suspend fun createPeriod(
        @Body request: CreatePeriodRequest,
    ): Response<PeriodResponse>

    @PUT("periods/{id}")
    suspend fun updatePeriod(
        @Path("id") id: String,
        @Body request: UpdatePeriodRequest,
    ): Response<PeriodResponse>

    @DELETE("periods/{id}")
    suspend fun deletePeriod(
        @Path("id") id: String,
    ): Response<Unit>

    @GET("periods/schedule")
    suspend fun getPeriodSchedule(): Response<PeriodScheduleResponse>

    @POST("periods/schedule")
    suspend fun createPeriodSchedule(
        @Body request: CreateScheduleRequest,
    ): Response<PeriodScheduleResponse>

    @PUT("periods/schedule")
    suspend fun updatePeriodSchedule(
        @Body request: CreateScheduleRequest,
    ): Response<PeriodScheduleResponse>

    @DELETE("periods/schedule")
    suspend fun deletePeriodSchedule(): Response<Unit>

    // Targets
    @GET("targets")
    suspend fun getTargets(
        @Query("periodId") periodId: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int? = null,
    ): Response<CategoryTargetsResponse>

    @POST("targets")
    suspend fun createTarget(
        @Body request: CreateTargetRequest,
    ): Response<TargetResponse>

    @PUT("targets/{id}")
    suspend fun updateTarget(
        @Path("id") id: String,
        @Body request: UpdateTargetRequest,
    ): Response<TargetResponse>

    // Settings
    @GET("settings/profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @PUT("settings/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest,
    ): Response<ProfileResponse>

    @GET("settings/preferences")
    suspend fun getPreferences(): Response<PreferencesResponse>

    @PUT("settings/preferences")
    suspend fun updatePreferences(
        @Body request: UpdatePreferencesRequest,
    ): Response<PreferencesResponse>

    @DELETE("settings/account")
    suspend fun deleteUserAccount(): Response<Unit>

    // Overlays
    @GET("overlays")
    suspend fun getOverlays(
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int? = null,
    ): Response<PaginatedOverlays>

    @POST("overlays")
    suspend fun createOverlay(
        @Body request: CreateOverlayRequest,
    ): Response<OverlayResponse>

    @PUT("overlays/{id}")
    suspend fun updateOverlay(
        @Path("id") id: String,
        @Body request: UpdateOverlayRequest,
    ): Response<OverlayResponse>

    @DELETE("overlays/{id}")
    suspend fun deleteOverlay(
        @Path("id") id: String,
    ): Response<Unit>

    // Onboarding
    @GET("onboarding/status")
    suspend fun getOnboardingStatus(): Response<OnboardingStatusResponse>

    @POST("onboarding/complete")
    suspend fun completeOnboarding(): Response<OnboardingCompleteResponse>
}
