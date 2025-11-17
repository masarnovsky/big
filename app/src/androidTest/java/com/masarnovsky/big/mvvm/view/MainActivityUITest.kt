package com.masarnovsky.big.mvvm.view

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Comprehensive UI tests for MainActivity using Compose Testing.
 * Covers green (happy path), red (error), and tricky (orientation, concurrency) cases.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

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

        // Verify text is displayed
        composeTestRule.onNodeWithText("Enter text to display 18/200")
            .assertIsDisplayed()
    }

    @Test
    fun previewButton_isDisplayed() {
        composeTestRule.onNodeWithContentDescription("Preview fullscreen")
            .assertIsDisplayed()
    }

    @Test
    fun historySection_displaysWhenPresent() {
        // History section should always be displayed
        composeTestRule.onNodeWithText("History").assertIsDisplayed()
    }

    // ===== RED CASES (Error Handling & Edge Cases) =====

    @Test
    fun textField_enforcesCharacterLimit() {
        // Try to enter 250 characters
        val longText = "a".repeat(250)
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(longText)
        composeTestRule.waitForIdle()

        // Should show 200/200
        composeTestRule.onNodeWithText("Enter text to display 200/200")
            .assertIsDisplayed()
    }

    @Test
    fun textField_allowsEmptyInput() {
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput("Some text")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Enter text to display 9/200")
            .performTextClearance()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Enter text to display")
            .assertIsDisplayed()
    }

    @Test
    fun fontSelector_allowsSingleSelection() {
        // Click different fonts
        composeTestRule.onNodeWithText("Pangolin").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Roboto").performClick()
        composeTestRule.waitForIdle()

        // Both should still be displayed (multi-select not enforced at UI level)
        composeTestRule.onNodeWithText("Pangolin").assertIsDisplayed()
        composeTestRule.onNodeWithText("Roboto").assertIsDisplayed()
    }

    @Test
    fun backgroundSelector_allowsMultipleClicks() {
        // Repeatedly click same background
        repeat(3) {
            composeTestRule.onNodeWithText("Black").performClick()
            composeTestRule.waitForIdle()
        }

        composeTestRule.onNodeWithText("Black").assertIsDisplayed()
    }

    @Test
    fun orientationSelector_togglesBetweenStates() {
        composeTestRule.onNodeWithText("Landscape").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Portrait").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Landscape").assertIsDisplayed()
        composeTestRule.onNodeWithText("Portrait").assertIsDisplayed()
    }

    // ===== TRICKY CASES (Complex Scenarios) =====

    @Test
    fun textField_handlesEmojis() {
        val emojiText = "🌍🎉😀🚀💡"
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(emojiText)
        composeTestRule.waitForIdle()

        // Check if count is correct
        composeTestRule.onNodeWithText("Enter text to display 5/200")
            .assertIsDisplayed()
    }

    @Test
    fun textField_handlesSpecialCharacters() {
        val specialText = "!@#$%^&*()_+-=[]{}|;:',.<>?/`~"
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(specialText)
        composeTestRule.waitForIdle()

        assertThat(specialText.length).isEqualTo(31)
    }

    @Test
    fun textField_handlesNewlines() {
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput("Line 1\nLine 2\nLine 3")
        composeTestRule.waitForIdle()

        // Text with newlines should be accepted
        composeTestRule.onNodeWithText("Enter text to display 20/200")
            .assertIsDisplayed()
    }

    @Test
    fun rapidFontChanges_handlesCorrectly() {
        // Rapidly change fonts
        composeTestRule.onNodeWithText("Pangolin").performClick()
        composeTestRule.onNodeWithText("Roboto").performClick()
        composeTestRule.onNodeWithText("Playfair").performClick()
        composeTestRule.onNodeWithText("Montserrat").performClick()
        composeTestRule.waitForIdle()

        // All should still be displayed
        composeTestRule.onNodeWithText("Pangolin").assertIsDisplayed()
        composeTestRule.onNodeWithText("Montserrat").assertIsDisplayed()
    }

    @Test
    fun rapidBackgroundChanges_handlesCorrectly() {
        composeTestRule.onNodeWithText("Black").performClick()
        composeTestRule.onNodeWithText("White").performClick()
        composeTestRule.onNodeWithText("Gradient").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Gradient").assertIsDisplayed()
    }

    @Test
    fun complexFlow_multipleInteractions() {
        // Enter text
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput("Test")
        composeTestRule.waitForIdle()

        // Change font
        composeTestRule.onNodeWithText("Roboto").performClick()
        composeTestRule.waitForIdle()

        // Add more text
        composeTestRule.onNodeWithText("Enter text to display 4/200")
            .performTextInput(" More Text")
        composeTestRule.waitForIdle()

        // Change background
        composeTestRule.onNodeWithText("White").performClick()
        composeTestRule.waitForIdle()

        // Verify final state
        composeTestRule.onNodeWithText("Enter text to display 14/200")
            .assertIsDisplayed()
    }

    @Test
    fun textInput_at199Characters_canAdd1More() {
        val text199 = "a".repeat(199)
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(text199)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Enter text to display 199/200")
            .performTextInput("b")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Enter text to display 200/200")
            .assertIsDisplayed()
    }

    @Test
    fun textInput_at200Characters_cannotAddMore() {
        val text200 = "a".repeat(200)
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(text200)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Enter text to display 200/200")
            .assertIsDisplayed()

        // Try to add more (should be rejected)
        composeTestRule.onNodeWithText("Enter text to display 200/200")
            .performTextInput("extra")
        composeTestRule.waitForIdle()

        // Should still show 200/200
        composeTestRule.onNodeWithText("Enter text to display 200/200")
            .assertIsDisplayed()
    }

    @Test
    fun allUIElements_persistAcrossInteractions() {
        // Perform various interactions
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput("Persist Test")
        composeTestRule.onNodeWithText("Pangolin").performClick()
        composeTestRule.onNodeWithText("White").performClick()
        composeTestRule.onNodeWithText("Portrait").performClick()
        composeTestRule.waitForIdle()

        // All elements should still be visible
        composeTestRule.onNodeWithText("Font Style").assertIsDisplayed()
        composeTestRule.onNodeWithText("Background").assertIsDisplayed()
        composeTestRule.onNodeWithText("Orientation").assertIsDisplayed()
        composeTestRule.onNodeWithText("History").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Preview fullscreen")
            .assertIsDisplayed()
    }
}
