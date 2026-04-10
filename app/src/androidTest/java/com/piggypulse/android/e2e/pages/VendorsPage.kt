package com.piggypulse.android.e2e.pages

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry

class VendorsPage(private val rule: ComposeTestRule) {

    fun navigateTo() {
        // Tap More tab — if deep in nav stack, tap it again to pop to root
        rule.onNodeWithTag("nav-moretab").performClick()
        rule.waitForIdle()
        Thread.sleep(500)

        // If more-vendors not visible, tap More again (pops to root)
        if (rule.onAllNodes(hasTestTag("more-vendors")).fetchSemanticsNodes().isEmpty()) {
            rule.onNodeWithTag("nav-moretab").performClick()
            rule.waitForIdle()
            Thread.sleep(500)
        }

        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodes(hasTestTag("more-vendors")).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag("more-vendors").performClick()
        rule.waitForIdle()
    }

    fun createVendor(name: String) {
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodes(hasTestTag("vendors-add-button")).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag("vendors-add-button").performClick()
        rule.waitForIdle()

        rule.onNodeWithTag("vendor-name-input").performTextInput(name)
        rule.onNodeWithTag("vendor-form-submit").performClick()
        rule.waitForIdle()
        Thread.sleep(1000)
    }

    fun expectVendorVisible(name: String) {
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodes(androidx.compose.ui.test.hasText(name)).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithText(name).assertIsDisplayed()
    }
}
