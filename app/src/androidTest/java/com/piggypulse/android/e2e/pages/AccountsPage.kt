package com.piggypulse.android.e2e.pages

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput

class AccountsPage(private val rule: ComposeTestRule) {

    fun navigateTo() {
        rule.onNodeWithTag("nav-accountstab").performClick()
        rule.waitForIdle()
    }

    fun createAccount(name: String, balance: String) {
        rule.onNodeWithTag("accounts-add-button").performClick()
        rule.waitForIdle()

        rule.onNodeWithTag("account-name-input").performTextInput(name)
        rule.onNodeWithTag("account-balance-input").performTextInput(balance)
        rule.onNodeWithTag("account-form-submit").performClick()
        rule.waitForIdle()
        Thread.sleep(1000)
    }

    fun expectAccountVisible(name: String) {
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodes(androidx.compose.ui.test.hasText(name))
                .fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithText(name).assertIsDisplayed()
    }
}
