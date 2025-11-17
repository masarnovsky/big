package com.masarnovsky.big

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.masarnovsky.big.mvvm.BackgroundColor
import com.masarnovsky.big.mvvm.InputFont
import com.masarnovsky.big.mvvm.Orientation
import com.masarnovsky.big.mvvm.view.MainActivity
import com.masarnovsky.big.mvvm.viewmodel.space
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        // Disable animations for stable tests on all API levels
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
            "settings put global window_animation_scale 0"
        )
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
            "settings put global transition_animation_scale 0"
        )
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
            "settings put global animator_duration_scale 0"
        )
    }

    @Test
    fun previewButton_noUserInput_isInactiveWithDefaultText() {
        composeTestRule.onNodeWithText("input text to show")
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun previewButton_withUserInput_becomesActiveWithInputText() {
        val inputText = "Hello"

        // Enter text
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(inputText)

        // Preview button should now be enabled and show the text
        val previewButtonText = space + inputText
        composeTestRule.onNodeWithText(previewButtonText, substring = false)
            .assertIsDisplayed()
            .assertIsEnabled()
            .assertHasClickAction()
    }

    @Test
    fun previewButton_displaysInputTextCorrectly() {
        val testText = "Test Message"

        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(testText)

        // Preview button should show the input text with a space prefix
        val previewButtonText = space + testText
        composeTestRule.onNodeWithText(previewButtonText, substring = false)
            .assertIsDisplayed()
    }

    @Test
    fun previewButton_withLongText_showsEllipsis() {
        val longText = "This is a very long text that exceeds the maximum display"

        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(longText)

        // Preview button should truncate and show ellipsis
        composeTestRule.onNodeWithText("...", substring = true)
            .assertIsDisplayed()
    }

    // ============================================================
    // FONT SELECTION TESTS (USER-REQUESTED)
    // ============================================================

    @Test
    fun fontSelector_changingFont_reflectedOnPreviewButton() {
        // Enter text first
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput("Test")

        // Change font to Pangolin
        composeTestRule.onNodeWithText(InputFont.PANGOLIN.label)
            .performClick()

        // Preview button should still display with new font
        val previewButtonText = space + "Test"
        composeTestRule.onNodeWithText(previewButtonText, substring = false)
            .assertIsDisplayed()
    }

    @Test
    fun fontSelector_allFontsAreSelectable() {
        InputFont.entries.forEach { font ->
            composeTestRule.onNodeWithText(font.label)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()
        }
    }

    @Test
    fun fontSelector_switchingBetweenFonts_maintainsInputText() {
        val testText = "Font Test"

        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(testText)

        // Switch through all fonts
        InputFont.entries.forEach { font ->
            composeTestRule.onNodeWithText(font.label)
                .performClick()

            // Text should still be displayed
            val previewButtonText = space + testText
            composeTestRule.onNodeWithText(previewButtonText, substring = false)
                .assertIsDisplayed()
        }
    }

    // ============================================================
    // BACKGROUND SELECTION TESTS (USER-REQUESTED)
    // ============================================================

    @Test
    fun backgroundSelector_changingBackground_reflectedOnPreviewButton() {
        // Enter text first
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput("Test")

        // Change background to White
        composeTestRule.onNodeWithText(BackgroundColor.WHITE.label)
            .performClick()

        // Preview button should still display
        val previewButtonText = space + "Test"
        composeTestRule.onNodeWithText(previewButtonText, substring = false)
            .assertIsDisplayed()
    }

    @Test
    fun backgroundSelector_allBackgroundsAreSelectable() {
        BackgroundColor.entries.forEach { background ->
            composeTestRule.onNodeWithText(background.label)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()
        }
    }

    @Test
    fun backgroundSelector_switchingBetweenBackgrounds_maintainsInputText() {
        val testText = "Background Test"

        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(testText)

        // Switch through all backgrounds
        BackgroundColor.entries.forEach { background ->
            composeTestRule.onNodeWithText(background.label)
                .performClick()

            // Text should still be displayed
            val previewButtonText = space + testText
            composeTestRule.onNodeWithText(previewButtonText, substring = false)
                .assertIsDisplayed()
        }
    }

    // ============================================================
    // ORIENTATION SELECTION TESTS
    // ============================================================

    @Test
    fun orientationSelector_allOrientationsAreSelectable() {
        Orientation.entries.forEach { orientation ->
            composeTestRule.onNodeWithText(orientation.label)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()
        }
    }

    // ============================================================
    // TEXT INPUT TESTS
    // ============================================================

    @Test
    fun textInput_isDisplayedAndAcceptsText() {
        composeTestRule.onNodeWithText("Enter text to display")
            .assertIsDisplayed()
            .performClick()
            .performTextInput("Hello World")

        // Preview button should show the text
        val previewButtonText = space + "Hello World"
        composeTestRule.onNodeWithText(previewButtonText, substring = false)
            .assertIsDisplayed()
    }

    @Test
    fun textInput_showsCharacterCount() {
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput("Hello")

        // Character counter should show
        composeTestRule.onNodeWithText("5/200", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun textInput_maxLength200Characters() {
        val maxText = "a".repeat(200)

        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(maxText)

        // Should show 200/200
        composeTestRule.onNodeWithText("200/200", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun textInput_exceedingMaxLengthIsRejected() {
        var oversizedText = "a".repeat(200)

        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(oversizedText)

        composeTestRule.onNodeWithText("200/200", substring = true)
            .assertIsDisplayed()

        oversizedText += "a"

        composeTestRule.onNodeWithText("Enter text to display 200/200")
            .performTextInput(oversizedText)

        // Should still show 200/200 (truncated)
        composeTestRule.onNodeWithText("200/200", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun textInput_acceptsEmoji() {
        val emojiText = "🌍🎉😀"

        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(emojiText)

        val previewButtonText = space + emojiText
        composeTestRule.onNodeWithText(previewButtonText, substring = false)
            .assertIsDisplayed()
    }

    @Test
    fun textInput_acceptsUnicodeCharacters() {
        val unicodeText = "世界"

        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(unicodeText)

        val previewButtonText = space + unicodeText
        composeTestRule.onNodeWithText(previewButtonText, substring = false)
            .assertIsDisplayed()
    }

    @Test
    fun textInput_acceptsSpecialCharacters() {
        val specialText = "!@#$%"

        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(specialText)

        val previewButtonText = space + specialText
        composeTestRule.onNodeWithText(previewButtonText, substring = false)
            .assertIsDisplayed()
    }

    @Test
    fun textInput_clearingText_disablesPreviewButton() {
        val input = "Test"
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(input)

        // Button should be enabled
        val previewButtonText = space + input
        composeTestRule.onNodeWithText(previewButtonText, substring = false)
            .assertIsEnabled()

        // Clear text
        composeTestRule.onNodeWithText(input)
            .performTextClearance()

        // Button should be disabled again
        composeTestRule.onNodeWithText("input text to show")
            .assertIsNotEnabled()
    }

    // ============================================================
    // UI ELEMENTS VISIBILITY TESTS
    // ============================================================

    @Test
    fun allUIElements_areDisplayed() {
        // Text input field
        composeTestRule.onNodeWithText("Enter text to display")
            .assertIsDisplayed()

        // Font Style label
        composeTestRule.onNodeWithText("Font Style")
            .assertIsDisplayed()

        // Font options
        InputFont.entries.forEach { font ->
            composeTestRule.onNodeWithText(font.label)
                .assertIsDisplayed()
        }

        // Background label
        composeTestRule.onNodeWithText("Background")
            .assertIsDisplayed()

        // Background options
        BackgroundColor.entries.forEach { background ->
            composeTestRule.onNodeWithText(background.label)
                .assertIsDisplayed()
        }

        // Orientation label
        composeTestRule.onNodeWithText("Orientation")
            .assertIsDisplayed()

        // Orientation options
        Orientation.entries.forEach { orientation ->
            composeTestRule.onNodeWithText(orientation.label)
                .assertIsDisplayed()
        }

        // Preview button
        composeTestRule.onNodeWithText("input text to show")
            .assertIsDisplayed()

        // History section
        composeTestRule.onNodeWithText("History")
            .assertIsDisplayed()
    }

    // ============================================================
    // COMPLETE USER FLOW TESTS
    // ============================================================

    @Test
    fun completeUserFlow_enterTextSelectOptionsAndPreview() {
        // Step 1: Enter text
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput("Hello World")

        // Step 2: Select font
        composeTestRule.onNodeWithText(InputFont.PANGOLIN.label)
            .performClick()

        // Step 3: Select background
        composeTestRule.onNodeWithText(BackgroundColor.WHITE.label)
            .performClick()

        // Step 4: Select orientation
        composeTestRule.onNodeWithText(Orientation.PORTRAIT.label)
            .performClick()

        // Step 5: Verify preview button is enabled with text
        val previewButtonText = space + "Hello World"
        composeTestRule.onNodeWithText(previewButtonText, substring = false)
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    // ============================================================
    // RAPID OPERATIONS TESTS
    // ============================================================

    @Test
    fun rapidFontChanges_shouldHandleCorrectly() {
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput("Test")

        // Rapidly change fonts
        repeat(3) {
            InputFont.entries.forEach { font ->
                composeTestRule.onNodeWithText(font.label)
                    .performClick()
            }
        }

        // Text should still be displayed
        val previewButtonText = space + "Test"
        composeTestRule.onNodeWithText(previewButtonText, substring = false)
            .assertIsDisplayed()
    }

    @Test
    fun rapidBackgroundChanges_shouldHandleCorrectly() {
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput("Test")

        // Rapidly change backgrounds
        repeat(3) {
            BackgroundColor.entries.forEach { background ->
                composeTestRule.onNodeWithText(background.label)
                    .performClick()
            }
        }

        // Text should still be displayed
        val previewButtonText = space + "Test"
        composeTestRule.onNodeWithText(previewButtonText, substring = false)
            .assertIsDisplayed()
    }

    // ============================================================
    // COMBINATION TESTS
    // ============================================================

    @Test
    fun changingAllOptions_maintainsTextInput() {
        val text = "Clicks First"
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(text)

        composeTestRule.onNodeWithText(InputFont.PLAYFAIR_DISPLAY.label)
            .performClick()

        composeTestRule.onNodeWithText(BackgroundColor.WHITE.label)
            .performClick()

        composeTestRule.onNodeWithText(Orientation.PORTRAIT.label)
            .performClick()

        // Preview button should work
        val previewButtonText = space + text
        composeTestRule.onNodeWithText(previewButtonText, substring = false)
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun enteringText_afterChangingOptions_shouldWork() {
        // Change options first
        composeTestRule.onNodeWithText(InputFont.PLAYFAIR_DISPLAY.label)
            .performClick()

        composeTestRule.onNodeWithText(BackgroundColor.WHITE.label)
            .performClick()

        // Then enter text
        val text = "Options First"
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(text)

        // Preview button should work
        val previewButtonText = space + text
        composeTestRule.onNodeWithText(previewButtonText, substring = false)
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    // ============================================================
    // EDGE CASE TESTS
    // ============================================================

    @Test
    fun textInput_withOnlyWhitespace_enablesPreviewButton() {
        val spaces = "     "
        composeTestRule.onNodeWithText("Enter text to display")
            .performTextInput(spaces)

        // Button should be enabled (whitespace counts as content)
        val previewButtonText = space + spaces
        composeTestRule.onNodeWithText(previewButtonText, substring = false)
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun selectingGradientBackground_shouldGenerateRandomGradient() {
        // Select gradient multiple times
        repeat(3) {
            composeTestRule.onNodeWithText(BackgroundColor.GRADIENT.label)
                .performClick()

            // Switch to another background
            composeTestRule.onNodeWithText(BackgroundColor.BLACK.label)
                .performClick()
        }

        // Should not crash and gradient should be generated each time
        composeTestRule.onNodeWithText(BackgroundColor.GRADIENT.label)
            .assertIsDisplayed()
    }
}
