package com.piggypulse.android.e2e.pages

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput

class VendorsPage(private val rule: ComposeTestRule) {

    fun navigateTo() {
        rule.onNodeWithTag("nav-moretab").performClick()
        rule.waitForIdle()
        rule.onNodeWithTag("more-vendors").performClick()
        rule.waitForIdle()
    }

    fun createVendor(name: String) {
        rule.onNodeWithTag("vendors-add-button").performClick()
        rule.waitForIdle()

        rule.onNodeWithTag("vendor-name-input").performTextInput(name)
        rule.onNodeWithTag("vendor-form-submit").performClick()
        rule.waitForIdle()
        Thread.sleep(1000)
    }

    fun expectVendorVisible(name: String) {
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodes(androidx.compose.ui.test.hasText(name))
                .fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithText(name).assertIsDisplayed()
    }
}
