package com.piggypulse.android.e2e.pages

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick

class OnboardingPage(private val rule: ComposeTestRule) {

    fun completeOnboarding() {
        try {
            rule.onNodeWithTag("onboarding-complete").performClick()
            rule.waitForIdle()
        } catch (_: Exception) {
            // Already past onboarding
        }
    }

    fun expectDashboard() {
        rule.waitUntil(timeoutMillis = 15_000) {
            rule.onAllNodes(androidx.compose.ui.test.hasTestTag("nav-dashboardtab"))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }
}
