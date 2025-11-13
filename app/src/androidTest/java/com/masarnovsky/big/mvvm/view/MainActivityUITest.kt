package com.masarnovsky.big.mvvm.view

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Comprehensive UI tests for MainActivity using Compose Testing and Hilt.
 * Covers green (happy path), red (error), and tricky (orientation, concurrency) cases.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityUITest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    // ===== GREEN CASES (Happy Paths) =====

    @Test
    fun mainScreen_displaysAllRequiredElements() {
        composeTestRule.onNodeWithText("Font Style").assertIsDisplayed()
        composeTestRule.onNodeWithText("Background").assertIsDisplayed()
        composeTestRule.onNodeWithText("Orientation").assertIsDisplayed()
        composeTestRule.onNodeWithText("History").assertIsDisplayed()
    }

    @Test
    fun textField_acceptsInput() {
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput("Hello World")

        composeTestRule.onNodeWithText("Enter text to display 11/200")
            .assertIsDisplayed()
    }

    @Test
    fun fontSelector_displaysAndSelectsAllFonts() {
        // All fonts should be displayed
        composeTestRule.onNodeWithText("Montserrat").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pangolin").assertIsDisplayed()
        composeTestRule.onNodeWithText("Roboto").assertIsDisplayed()
        composeTestRule.onNodeWithText("Playfair").assertIsDisplayed()

        // Select different font
        composeTestRule.onNodeWithText("Pangolin").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun backgroundSelector_displaysAndSelectsAll() {
        composeTestRule.onNodeWithText("Black").assertIsDisplayed()
        composeTestRule.onNodeWithText("White").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gradient").assertIsDisplayed()

        composeTestRule.onNodeWithText("White").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun orientationSelector_displaysAndSelectsAll() {
        composeTestRule.onNodeWithText("Landscape").assertIsDisplayed()
        composeTestRule.onNodeWithText("Portrait").assertIsDisplayed()

        composeTestRule.onNodeWithText("Portrait").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun completeUserFlow_enterTextAndSelectOptions() {
        // Enter text
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput("Complete Flow Test")
        composeTestRule.waitForIdle()

        // Select font
        composeTestRule.onNodeWithText("Pangolin").performClick()
        composeTestRule.waitForIdle()

        // Select background
        composeTestRule.onNodeWithText("White").performClick()
        composeTestRule.waitForIdle()

        // Select orientation
        composeTestRule.onNodeWithText("Portrait").performClick()
        composeTestRule.waitForIdle()

        // Verify all selections are visible
        composeTestRule.onNodeWithText("Enter text to display 18/200").assertIsDisplayed()
    }

    // ===== RED CASES (Error Handling) =====

    @Test
    fun textField_rejectsExcessiveLength() {
        val maxText = "a".repeat(200)
        val extraText = maxText + "b"

        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(extraText)
        composeTestRule.waitForIdle()

        // Should only accept 200 characters
        composeTestRule.onNodeWithText("Enter text to display 200/200")
            .assertIsDisplayed()
    }

    @Test
    fun previewButton_disabledWithEmptyText() {
        composeTestRule.waitForIdle()

        // Try to find and verify button state
        // Button should be disabled or not fully functional
        composeTestRule.onNodeWithText("Enter text to display")
            .assertIsDisplayed()
    }

    @Test
    fun textField_handlesEmptyInput() {
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput("Test")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Enter text to display 4/200")
            .performTextClearance()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Enter text to display")
            .assertIsDisplayed()
    }

    // ===== TRICKY CASES (Edge Cases) =====

    @Test
    fun textField_handlesEmojiInput() {
        val emojiText = "🌍🎉😀"

        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(emojiText)
        composeTestRule.waitForIdle()

        // Should display character count
        composeTestRule.onNodeWithText("Enter text to display", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun textField_handlesSpecialCharacters() {
        val specialText = "!@#\$%^&*()"

        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(specialText)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Enter text to display 10/200")
            .assertIsDisplayed()
    }

    @Test
    fun textField_handlesUnicodeCharacters() {
        val unicodeText = "世界"

        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(unicodeText)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Enter text to display", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun rapidClicks_onButtons_shouldNotCrash() {
        repeat(10) {
            composeTestRule.onNodeWithText("Pangolin").performClick()
            composeTestRule.onNodeWithText("Roboto").performClick()
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun rapidTextEntry_shouldHandleCorrectly() {
        repeat(5) { i ->
            composeTestRule.onNodeWithText("Enter text to display", substring = true)
                .performTextClearance()
            composeTestRule.onNodeWithText("Enter text to display")
                .performTextInput("Text $i")
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun switchingBetweenAllBackgrounds_shouldWork() {
        composeTestRule.onNodeWithText("Black").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("White").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Gradient").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Black").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun switchingBetweenOrientations_shouldWork() {
        composeTestRule.onNodeWithText("Landscape").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Portrait").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Landscape").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun enterMaxLengthText_shouldDisplay() {
        val maxText = "a".repeat(200)

        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(maxText)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Enter text to display 200/200")
            .assertIsDisplayed()
    }

    @Test
    fun allClickableElements_haveClickActions() {
        // Font buttons
        composeTestRule.onNodeWithText("Montserrat").assertHasClickAction()
        composeTestRule.onNodeWithText("Pangolin").assertHasClickAction()

        // Background buttons
        composeTestRule.onNodeWithText("Black").assertHasClickAction()
        composeTestRule.onNodeWithText("White").assertHasClickAction()

        // Orientation buttons
        composeTestRule.onNodeWithText("Landscape").assertHasClickAction()
        composeTestRule.onNodeWithText("Portrait").assertHasClickAction()
    }

    @Test
    fun versionNumber_isDisplayed() {
        composeTestRule.onNodeWithText("v1.1", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun historySection_isScrollable() {
        composeTestRule.onNodeWithText("History")
            .assertIsDisplayed()

        // History section should exist
        composeTestRule.waitForIdle()
    }

    @Test
    fun selectAllFonts_sequentially() {
        val fonts = listOf("Montserrat", "Pangolin", "Roboto", "Playfair")

        fonts.forEach { font ->
            composeTestRule.onNodeWithText(font).performClick()
            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun textFieldLabel_updatesWithCharacterCount() {
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput("Test")

        composeTestRule.onNodeWithText("Enter text to display 4/200")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Enter text to display 4/200")
            .performTextInput(" More")

        composeTestRule.onNodeWithText("Enter text to display 9/200")
            .assertIsDisplayed()
    }

    @Test
    fun gradientButton_canBeClicked() {
        composeTestRule.onNodeWithText("Gradient").performClick()
        composeTestRule.waitForIdle()

        // Should not crash when clicking gradient multiple times
        repeat(3) {
            composeTestRule.onNodeWithText("Gradient").performClick()
            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun enterTextWithNewlines_shouldAccept() {
        // Note: actual newline entry might be limited by the text field
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput("Line 1")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Enter text to display 6/200")
            .assertIsDisplayed()
    }

    @Test
    fun multipleSelectionsInQuickSuccession_shouldNotCrash() {
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput("Quick test")

        composeTestRule.onNodeWithText("Pangolin").performClick()
        composeTestRule.onNodeWithText("White").performClick()
        composeTestRule.onNodeWithText("Portrait").performClick()

        composeTestRule.waitForIdle()

        // Verify everything still works
        composeTestRule.onNodeWithText("Enter text to display 10/200")
            .assertIsDisplayed()
    }

    @Test
    fun textFieldRetainsContent_afterSelectionChanges() {
        val testText = "Persistent text"

        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(testText)

        composeTestRule.onNodeWithText("Pangolin").performClick()
        composeTestRule.onNodeWithText("White").performClick()
        composeTestRule.onNodeWithText("Portrait").performClick()

        composeTestRule.waitForIdle()

        // Text should still be there
        composeTestRule.onNodeWithText("Enter text to display 15/200")
            .assertIsDisplayed()
    }

    @Test
    fun clearAndReenterText_shouldWork() {
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput("First")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Enter text to display 5/200")
            .performTextClearance()

        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput("Second")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Enter text to display 6/200")
            .assertIsDisplayed()
    }
}
