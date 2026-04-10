package com.piggypulse.android.e2e.pages

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput

class AuthPage(private val rule: ComposeTestRule) {

    fun login(email: String, password: String) {
        rule.onNodeWithTag("login-email").performTextInput(email)
        rule.onNodeWithTag("login-password").performTextInput(password)
        rule.onNodeWithTag("login-submit").performClick()
    }

    fun expectDashboardOrOnboarding() {
        rule.waitUntil(timeoutMillis = 15_000) {
            rule.onAllNodesWithTag("nav-dashboardtab").fetchSemanticsNodes().isNotEmpty() ||
                rule.onAllNodesWithTag("onboarding-complete").fetchSemanticsNodes().isNotEmpty()
        }
    }

    fun register(name: String, email: String, password: String) {
        rule.onNodeWithTag("register-link").performClick()
        rule.waitForIdle()
        rule.onNodeWithTag("register-name").performTextInput(name)
        rule.onNodeWithTag("register-email").performTextInput(email)
        rule.onNodeWithTag("register-password").performTextInput(password)
        rule.onNodeWithTag("register-confirm-password").performTextInput(password)
        rule.onNodeWithTag("register-submit").performClick()
    }

    fun logout() {
        // Navigate to More tab
        rule.onNodeWithTag("nav-moretab").performClick()
        rule.waitForIdle()
        // Tap logout
        rule.onNodeWithTag("logout-button").performClick()
        rule.waitForIdle()
    }

    fun expectLoginScreen() {
        rule.onNodeWithTag("login-email").assertIsDisplayed()
    }
}

private fun ComposeTestRule.onAllNodesWithTag(tag: String) =
    onAllNodes(androidx.compose.ui.test.hasTestTag(tag))
