package com.piggypulse.android.e2e.tests

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.piggypulse.android.MainActivity
import com.piggypulse.android.e2e.helpers.ApiHelper
import com.piggypulse.android.e2e.helpers.clearAppData
import com.piggypulse.android.e2e.pages.AccountsPage
import com.piggypulse.android.e2e.pages.AuthPage
import com.piggypulse.android.e2e.pages.CategoriesPage
import com.piggypulse.android.e2e.pages.OnboardingPage
import com.piggypulse.android.e2e.pages.VendorsPage
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class StructureTests {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var auth: AuthPage
    private lateinit var accounts: AccountsPage
    private lateinit var categories: CategoriesPage
    private lateinit var vendors: VendorsPage

    @Before
    fun setUp() {
        hiltRule.inject()
        clearAppData()
        auth = AuthPage(composeRule)
        accounts = AccountsPage(composeRule)
        categories = CategoriesPage(composeRule)
        vendors = VendorsPage(composeRule)

        // Register + seed via API, then login via UI
        val user = ApiHelper.registerUser(name = "Structure Test")
        user.token?.let { ApiHelper.seedUserData(it) }
        auth.login(user.email, user.password)
        auth.expectDashboardOrOnboarding()

        val onboarding = OnboardingPage(composeRule)
        onboarding.completeOnboarding()
    }

    @Test
    fun testCreateAccount() {
        accounts.navigateTo()
        accounts.createAccount("Checking", "2000")
        accounts.expectAccountVisible("Checking")
    }

    @Test
    fun testCreateExpenseCategory() {
        categories.navigateTo()
        categories.createCategory("Groceries", "expense")
        categories.expectCategoryVisible("Groceries")
    }

    @Test
    fun testCreateIncomeCategory() {
        categories.navigateTo()
        categories.createCategory("Salary", "income")
        categories.expectCategoryVisible("Salary")
    }

    @Test
    fun testCreateVendor() {
        vendors.navigateTo()
        vendors.createVendor("Albert Heijn")
        vendors.expectVendorVisible("Albert Heijn")
    }
}
