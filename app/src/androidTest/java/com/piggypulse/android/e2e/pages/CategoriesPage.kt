package com.piggypulse.android.e2e.pages

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput

class CategoriesPage(private val rule: ComposeTestRule) {

    fun navigateTo() {
        rule.onNodeWithTag("more-nav-item").performClick()
        rule.waitForIdle()
        rule.onNodeWithTag("more-categories").performClick()
        rule.waitForIdle()
    }

    fun createCategory(name: String, type: String) {
        rule.onNodeWithTag("categories-add-button").performClick()
        rule.waitForIdle()

        // Select type
        val typeTag = "category-type-${type.lowercase()}"
        rule.onNodeWithTag(typeTag).performClick()

        rule.onNodeWithTag("category-name-input").performTextInput(name)
        rule.onNodeWithTag("category-form-submit").performClick()
        rule.waitForIdle()
        Thread.sleep(1000)
    }

    fun expectCategoryVisible(name: String) {
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodes(androidx.compose.ui.test.hasText(name))
                .fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithText(name).assertIsDisplayed()
    }
}
