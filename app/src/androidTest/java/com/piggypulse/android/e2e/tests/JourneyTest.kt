package com.piggypulse.android.e2e.tests

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.piggypulse.android.MainActivity
import com.piggypulse.android.e2e.helpers.ApiHelper
import com.piggypulse.android.e2e.pages.AccountsPage
import com.piggypulse.android.e2e.pages.AuthPage
import com.piggypulse.android.e2e.pages.CategoriesPage
import com.piggypulse.android.e2e.pages.OnboardingPage
import com.piggypulse.android.e2e.pages.TransactionsPage
import com.piggypulse.android.e2e.pages.VendorsPage
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class JourneyTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testFirstTimeUserJourney() {
        // Register + seed via API
        val user = ApiHelper.registerUser(name = "Journey User")
        val token = user.token ?: throw AssertionError("Registration failed — no token")
        ApiHelper.seedUserData(token)

        val auth = AuthPage(composeRule)
        val onboarding = OnboardingPage(composeRule)
        val accounts = AccountsPage(composeRule)
        val categories = CategoriesPage(composeRule)
        val vendors = VendorsPage(composeRule)
        val transactions = TransactionsPage(composeRule)

        // Login
        auth.login(user.email, user.password)
        auth.expectDashboardOrOnboarding()
        onboarding.completeOnboarding()

        // Create accounts
        accounts.navigateTo()
        accounts.createAccount("Checking", "2000")
        accounts.createAccount("Savings", "5000")
        accounts.expectAccountVisible("Checking")

        // Create categories
        categories.navigateTo()
        categories.createCategory("Groceries", "expense")
        categories.createCategory("Salary", "income")

        // Create vendor
        vendors.navigateTo()
        vendors.createVendor("Albert Heijn")
        vendors.expectVendorVisible("Albert Heijn")

        // Create transactions
        transactions.navigateTo()
        transactions.createTransaction("3000", "April Salary", "Salary", "Checking")

        transactions.navigateTo()
        transactions.createTransaction("85.50", "Weekly groceries", "Groceries", "Checking")

        // Logout + re-login
        auth.logout()
        auth.expectLoginScreen()
        auth.login(user.email, user.password)
        auth.expectDashboardOrOnboarding()
    }
}
