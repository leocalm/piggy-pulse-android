package com.piggypulse.android.e2e.pages

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput

class AuthPage(private val rule: ComposeTestRule) {

    fun login(email: String, password: String) {
        // Wait for login screen to appear (app may be loading)
        rule.waitUntil(timeoutMillis = 15_000) {
            rule.onAllNodes(hasTestTag("login-email")).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag("login-email").performTextInput(email)
        rule.onNodeWithTag("login-password").performTextInput(password)
        rule.onNodeWithTag("login-submit").performClick()
    }

    fun expectDashboardOrOnboarding() {
        rule.waitUntil(timeoutMillis = 15_000) {
            rule.onAllNodes(hasTestTag("nav-dashboardtab")).fetchSemanticsNodes().isNotEmpty() ||
                rule.onAllNodes(hasTestTag("onboarding-complete")).fetchSemanticsNodes().isNotEmpty()
        }
    }

    fun register(name: String, email: String, password: String) {
        // Wait for login screen then navigate to register
        rule.waitUntil(timeoutMillis = 15_000) {
            rule.onAllNodes(hasTestTag("register-link")).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag("register-link").performClick()
        rule.waitForIdle()

        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodes(hasTestTag("register-name")).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag("register-name").performTextInput(name)
        rule.onNodeWithTag("register-email").performTextInput(email)
        rule.onNodeWithTag("register-password").performTextInput(password)
        // Note: Android register screen may not have confirm-password field
        rule.onNodeWithTag("register-submit").performClick()
    }

    fun logout() {
        // Navigate away from More first to clear saved state, then back to More
        rule.onNodeWithTag("nav-dashboardtab").performClick()
        rule.waitForIdle()
        Thread.sleep(300)
        rule.onNodeWithTag("nav-moretab").performClick()
        rule.waitForIdle()
        rule.waitUntil(timeoutMillis = 15_000) {
            rule.onAllNodes(hasTestTag("logout-button")).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag("logout-button").performClick()
        rule.waitForIdle()
    }

    fun expectLoginScreen() {
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodes(hasTestTag("login-email")).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag("login-email").assertIsDisplayed()
    }
}
