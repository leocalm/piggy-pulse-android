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
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TransactionTests {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var auth: AuthPage
    private lateinit var accounts: AccountsPage
    private lateinit var categories: CategoriesPage
    private lateinit var transactions: TransactionsPage

    @Before
    fun setUp() {
        hiltRule.inject()
        auth = AuthPage(composeRule)
        accounts = AccountsPage(composeRule)
        categories = CategoriesPage(composeRule)
        transactions = TransactionsPage(composeRule)

        // Register + seed via API, then login via UI
        val user = ApiHelper.registerUser(name = "Transaction Test")
        user.token?.let { ApiHelper.seedUserData(it) }
        auth.login(user.email, user.password)
        auth.expectDashboardOrOnboarding()

        val onboarding = OnboardingPage(composeRule)
        onboarding.completeOnboarding()

        // Seed structure via UI
        accounts.navigateTo()
        accounts.createAccount("Checking", "5000")

        categories.navigateTo()
        categories.createCategory("Food", "expense")
        categories.createCategory("Salary", "income")
    }

    @Test
    fun testCreateExpenseTransaction() {
        transactions.navigateTo()
        transactions.createTransaction(
            amount = "42.50",
            description = "Lunch",
            category = "Food",
            account = "Checking",
        )
        transactions.expectTransactionVisible("Lunch")
    }

    @Test
    fun testCreateIncomeTransaction() {
        transactions.navigateTo()
        transactions.createTransaction(
            amount = "2500",
            description = "Monthly salary",
            category = "Salary",
            account = "Checking",
        )
        transactions.expectTransactionVisible("Monthly salary")
    }

    @Test
    fun testCreateTransferTransaction() {
        // Create second account
        accounts.navigateTo()
        accounts.createAccount("Savings", "0")

        transactions.navigateTo()
        transactions.createTransaction(
            amount = "500",
            description = "Move to savings",
            category = "Transfer",
            account = "Checking",
            isTransfer = true,
            toAccount = "Savings",
        )
        transactions.expectTransactionVisible("Move to savings")
    }
}
