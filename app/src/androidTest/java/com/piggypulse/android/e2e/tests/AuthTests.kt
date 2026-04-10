package com.piggypulse.android.e2e.tests

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.piggypulse.android.MainActivity
import com.piggypulse.android.e2e.helpers.ApiHelper
import com.piggypulse.android.e2e.helpers.TestConfig
import com.piggypulse.android.e2e.helpers.clearAppData
import com.piggypulse.android.e2e.pages.AuthPage
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AuthTests {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var auth: AuthPage

    @Before
    fun setUp() {
        hiltRule.inject()
        clearAppData()
        auth = AuthPage(composeRule)
    }

    /** Test 1: Login with valid credentials */
    @Test
    fun testLoginWithValidCredentials() {
        val user = ApiHelper.registerUser(name = "Login Test")
        auth.login(user.email, user.password)
        auth.expectDashboardOrOnboarding()
    }

    /** Test 2: Login with wrong password shows error */
    @Test
    fun testLoginWithWrongPassword() {
        val user = ApiHelper.registerUser(name = "Wrong PW Test")
        auth.login(user.email, "WrongPassword!123")
        // Should stay on login with error visible
        composeRule.waitForIdle()
        Thread.sleep(2000)
        auth.expectLoginScreen()
    }

    /** Test 3: Registration */
    @Test
    fun testRegistration() {
        val timestamp = System.currentTimeMillis()
        val email = "e2e-register-$timestamp@test.piggypulse.com"
        auth.register("Registration Test", email, TestConfig.TEST_PASSWORD)
        auth.expectDashboardOrOnboarding()
    }
}
