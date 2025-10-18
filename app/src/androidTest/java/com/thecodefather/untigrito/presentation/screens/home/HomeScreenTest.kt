package com.thecodefather.untigrito.presentation.screens.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.thecodefather.untigrito.ui.theme.UnTigritoTheme
import org.junit.Rule
import org.junit.Test

/**
 * Instrumented UI tests for HomeScreen
 *
 * Tests the Compose UI components of the HomeScreen.
 * These tests run on an Android device or emulator.
 */
class HomeScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysWelcomeText() {
        // Given: HomeScreen is rendered
        composeTestRule.setContent {
            UnTigritoTheme {
                HomeScreen(viewModel = HomeViewModel())
            }
        }

        // When/Then: The welcome text is visible
        composeTestRule.onNodeWithText("Welcome").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysAppTitle() {
        // Given: HomeScreen is rendered
        composeTestRule.setContent {
            UnTigritoTheme {
                HomeScreen(viewModel = HomeViewModel())
            }
        }

        // When/Then: The app title is visible in the top bar
        composeTestRule.onNodeWithText("UnTigritoApp").assertIsDisplayed()
    }
}

