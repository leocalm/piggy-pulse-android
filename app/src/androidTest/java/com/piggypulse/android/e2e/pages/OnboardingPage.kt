package com.piggypulse.android.e2e.pages

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick

class OnboardingPage(private val rule: ComposeTestRule) {

    fun completeOnboarding() {
        // Check if onboarding is shown; if not, we're already on dashboard
        val nodes = rule.onAllNodes(androidx.compose.ui.test.hasTestTag("onboarding-complete"))
            .fetchSemanticsNodes()
        if (nodes.isNotEmpty()) {
            rule.onNodeWithTag("onboarding-complete").performClick()
            rule.waitForIdle()
        }
    }

    fun expectDashboard() {
        rule.waitUntil(timeoutMillis = 15_000) {
            rule.onAllNodes(androidx.compose.ui.test.hasTestTag("nav-dashboardtab"))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }
}
