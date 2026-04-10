package com.piggypulse.android.e2e.pages

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput

class TransactionsPage(private val rule: ComposeTestRule) {

    fun navigateTo() {
        rule.onNodeWithTag("nav-transactionstab").performClick()
        rule.waitForIdle()
    }

    fun createTransaction(
        amount: String,
        description: String,
        category: String,
        account: String,
        isTransfer: Boolean = false,
        toAccount: String? = null,
    ) {
        rule.onNodeWithTag("transactions-add-button").performClick()
        rule.waitForIdle()

        if (isTransfer) {
            rule.onNodeWithTag("transaction-transfer-toggle").performClick()
        }

        rule.onNodeWithTag("transaction-amount-input").performTextInput(amount)
        rule.onNodeWithTag("transaction-description-input").performTextInput(description)

        // Select category (skip for transfers — auto-assigned)
        if (!isTransfer) {
            rule.onNodeWithTag("transaction-category-select").performClick()
            rule.waitForIdle()
            rule.onNodeWithText(category, substring = true).performClick()
            rule.waitForIdle()
        }

        // Select account
        rule.onNodeWithTag("transaction-account-select").performClick()
        rule.waitForIdle()
        rule.onNodeWithText(account, substring = true).performClick()
        rule.waitForIdle()

        // Select to-account for transfers
        if (isTransfer && toAccount != null) {
            rule.onNodeWithTag("transaction-to-account-select").performClick()
            rule.waitForIdle()
            rule.onNodeWithText(toAccount, substring = true).performClick()
            rule.waitForIdle()
        }

        rule.onNodeWithTag("transaction-form-submit").performClick()
        rule.waitForIdle()
        Thread.sleep(1000)
    }

    fun expectTransactionVisible(description: String) {
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodes(androidx.compose.ui.test.hasText(description))
                .fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithText(description).assertIsDisplayed()
    }
}
