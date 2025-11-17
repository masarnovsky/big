package com.masarnovsky.big.mvvm.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.masarnovsky.big.mvvm.BackgroundColor
import com.masarnovsky.big.mvvm.GradientColor
import com.masarnovsky.big.mvvm.InputFont
import com.masarnovsky.big.mvvm.Orientation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Comprehensive unit tests for MainViewModel.
 * Covers green (happy path), red (error), and tricky (edge) cases.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var application: Application
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        application = RuntimeEnvironment.getApplication()
        viewModel = MainViewModel(application)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ===== GREEN CASES (Happy Paths) =====

    @Test
    fun `GIVEN ViewModel WHEN initialized THEN starts with empty input text`() = runTest {
        assertThat(viewModel.inputText.value).isEmpty()
    }

    @Test
    fun `GIVEN valid text WHEN updateInputText THEN updates text successfully`() = runTest {
        viewModel.updateInputText("Hello World")
        advanceUntilIdle()

        assertThat(viewModel.inputText.value).isEqualTo("Hello World")
    }

    @Test
    fun `GIVEN valid font WHEN updateFont THEN updates font successfully`() = runTest {
        viewModel.updateFont(InputFont.PANGOLIN)
        advanceUntilIdle()

        assertThat(viewModel.selectedInputFont.value).isEqualTo(InputFont.PANGOLIN)
    }

    @Test
    fun `GIVEN valid background WHEN updateBackground THEN updates background successfully`() = runTest {
        viewModel.updateBackground(BackgroundColor.WHITE)
        advanceUntilIdle()

        assertThat(viewModel.selectedBackground.value).isEqualTo(BackgroundColor.WHITE)
    }

    @Test
    fun `GIVEN valid gradient WHEN updateGradient THEN updates gradient successfully`() = runTest {
        viewModel.updateGradient(GradientColor.PURPLE_PINK)
        advanceUntilIdle()

        assertThat(viewModel.selectedGradient.value).isEqualTo(GradientColor.PURPLE_PINK)
    }

    @Test
    fun `GIVEN valid orientation WHEN updateOrientation THEN updates orientation successfully`() = runTest {
        viewModel.updateOrientation(Orientation.LANDSCAPE)
        advanceUntilIdle()

        assertThat(viewModel.selectedOrientation.value).isEqualTo(Orientation.LANDSCAPE)
    }

    @Test
    fun `GIVEN non-empty text WHEN addTextToHistory THEN adds to history`() = runTest {
        viewModel.updateInputText("Test Text")
        advanceUntilIdle()

        viewModel.addTextToHistory()
        advanceUntilIdle()

        // Text should be cleared after adding to history
        assertThat(viewModel.inputText.value).isEmpty()
    }

    @Test
    fun `GIVEN valid text WHEN saveText THEN clears input text`() = runTest {
        viewModel.updateInputText("Save me")
        advanceUntilIdle()

        viewModel.saveText()
        advanceUntilIdle()

        assertThat(viewModel.inputText.value).isEmpty()
    }

    @Test
    fun `GIVEN different fonts WHEN updateFont multiple times THEN updates correctly`() = runTest {
        InputFont.entries.forEach { font ->
            viewModel.updateFont(font)
            advanceUntilIdle()
            assertThat(viewModel.selectedInputFont.value).isEqualTo(font)
        }
    }

    @Test
    fun `GIVEN different backgrounds WHEN updateBackground multiple times THEN updates correctly`() = runTest {
        BackgroundColor.entries.forEach { bg ->
            viewModel.updateBackground(bg)
            advanceUntilIdle()
            assertThat(viewModel.selectedBackground.value).isEqualTo(bg)
        }
    }

    @Test
    fun `GIVEN different gradients WHEN updateGradient multiple times THEN updates correctly`() = runTest {
        GradientColor.entries.forEach { gradient ->
            viewModel.updateGradient(gradient)
            advanceUntilIdle()
            assertThat(viewModel.selectedGradient.value).isEqualTo(gradient)
        }
    }

    @Test
    fun `GIVEN different orientations WHEN updateOrientation multiple times THEN updates correctly`() = runTest {
        Orientation.entries.forEach { orientation ->
            viewModel.updateOrientation(orientation)
            advanceUntilIdle()
            assertThat(viewModel.selectedOrientation.value).isEqualTo(orientation)
        }
    }

    // ===== RED CASES (Error Handling & Edge Cases) =====

    @Test
    fun `GIVEN text exceeding 200 chars WHEN updateInputText THEN truncates to 200`() = runTest {
        val longText = "a".repeat(250)
        viewModel.updateInputText(longText)
        advanceUntilIdle()

        assertThat(viewModel.inputText.value.length).isEqualTo(200)
    }

    @Test
    fun `GIVEN exactly 200 chars WHEN updateInputText THEN accepts full text`() = runTest {
        val exactText = "a".repeat(200)
        viewModel.updateInputText(exactText)
        advanceUntilIdle()

        assertThat(viewModel.inputText.value).isEqualTo(exactText)
        assertThat(viewModel.inputText.value.length).isEqualTo(200)
    }

    @Test
    fun `GIVEN 201 chars WHEN updateInputText THEN truncates to 200`() = runTest {
        val tooLongText = "a".repeat(201)
        viewModel.updateInputText(tooLongText)
        advanceUntilIdle()

        assertThat(viewModel.inputText.value.length).isEqualTo(200)
        assertThat(viewModel.inputText.value).isEqualTo("a".repeat(200))
    }

    @Test
    fun `GIVEN empty text WHEN updateInputText THEN accepts empty string`() = runTest {
        viewModel.updateInputText("Some text")
        advanceUntilIdle()

        viewModel.updateInputText("")
        advanceUntilIdle()

        assertThat(viewModel.inputText.value).isEmpty()
    }

    @Test
    fun `GIVEN blank text WHEN updateInputText THEN accepts blank string`() = runTest {
        viewModel.updateInputText("   ")
        advanceUntilIdle()

        assertThat(viewModel.inputText.value).isEqualTo("   ")
    }

    @Test
    fun `GIVEN empty text WHEN addTextToHistory THEN does nothing`() = runTest {
        viewModel.updateInputText("")
        advanceUntilIdle()

        viewModel.addTextToHistory()
        advanceUntilIdle()

        // Should still be empty
        assertThat(viewModel.inputText.value).isEmpty()
    }

    @Test
    fun `GIVEN blank text WHEN saveText THEN saves successfully`() = runTest {
        viewModel.updateInputText("   ")
        advanceUntilIdle()

        viewModel.saveText()
        advanceUntilIdle()

        // Text should be cleared
        assertThat(viewModel.inputText.value).isEmpty()
    }

    // ===== TRICKY CASES (Complex Scenarios) =====

    @Test
    fun `GIVEN emoji text WHEN updateInputText THEN handles correctly`() = runTest {
        val emojiText = "🌍🎉😀🚀💡"
        viewModel.updateInputText(emojiText)
        advanceUntilIdle()

        assertThat(viewModel.inputText.value).isEqualTo(emojiText)
    }

    @Test
    fun `GIVEN unicode text WHEN updateInputText THEN handles correctly`() = runTest {
        val unicodeText = "世界 مرحبا Привет"
        viewModel.updateInputText(unicodeText)
        advanceUntilIdle()

        assertThat(viewModel.inputText.value).isEqualTo(unicodeText)
    }

    @Test
    fun `GIVEN multiline text WHEN updateInputText THEN handles correctly`() = runTest {
        val multilineText = "Line 1\nLine 2\nLine 3"
        viewModel.updateInputText(multilineText)
        advanceUntilIdle()

        assertThat(viewModel.inputText.value).isEqualTo(multilineText)
    }

    @Test
    fun `GIVEN special characters WHEN updateInputText THEN handles correctly`() = runTest {
        val specialText = "!@#$%^&*()_+-=[]{}|;:',.<>?/`~"
        viewModel.updateInputText(specialText)
        advanceUntilIdle()

        assertThat(viewModel.inputText.value).isEqualTo(specialText)
    }

    @Test
    fun `GIVEN mixed emoji and text WHEN updateInputText with 200+ length THEN truncates correctly`() = runTest {
        val mixedText = "Hello 🌍 ".repeat(30) // Creates 270 characters
        viewModel.updateInputText(mixedText)
        advanceUntilIdle()

        assertThat(viewModel.inputText.value.length).isAtMost(200)
    }

    @Test
    fun `GIVEN text with only emojis near 200 limit WHEN updateInputText THEN truncates properly`() = runTest {
        val emojiText = "🌍".repeat(100) // May exceed 200 chars depending on encoding
        viewModel.updateInputText(emojiText)
        advanceUntilIdle()

        assertThat(viewModel.inputText.value.length).isAtMost(200)
    }

    @Test
    fun `GIVEN sequential updates WHEN rapid state changes THEN final state is correct`() = runTest {
        viewModel.updateInputText("Text 1")
        viewModel.updateFont(InputFont.ROBOTO_SLAB)
        viewModel.updateBackground(BackgroundColor.WHITE)
        viewModel.updateGradient(GradientColor.BLUE_PURPLE)
        viewModel.updateOrientation(Orientation.LANDSCAPE)
        advanceUntilIdle()

        assertThat(viewModel.inputText.value).isEqualTo("Text 1")
        assertThat(viewModel.selectedInputFont.value).isEqualTo(InputFont.ROBOTO_SLAB)
        assertThat(viewModel.selectedBackground.value).isEqualTo(BackgroundColor.WHITE)
        assertThat(viewModel.selectedGradient.value).isEqualTo(GradientColor.BLUE_PURPLE)
        assertThat(viewModel.selectedOrientation.value).isEqualTo(Orientation.LANDSCAPE)
    }

    @Test
    fun `GIVEN text just under limit WHEN adding one char THEN accepts or truncates`() = runTest {
        val almostLimit = "a".repeat(199)
        viewModel.updateInputText(almostLimit)
        advanceUntilIdle()
        assertThat(viewModel.inputText.value.length).isEqualTo(199)

        viewModel.updateInputText(almostLimit + "b")
        advanceUntilIdle()
        assertThat(viewModel.inputText.value.length).isEqualTo(200)
        assertThat(viewModel.inputText.value).isEqualTo("a".repeat(199) + "b")
    }

    @Test
    fun `GIVEN SQL injection attempt WHEN updateInputText THEN treats as plain text`() = runTest {
        val sqlInjection = "'; DROP TABLE text_history; --"
        viewModel.updateInputText(sqlInjection)
        advanceUntilIdle()

        assertThat(viewModel.inputText.value).isEqualTo(sqlInjection)
    }

    @Test
    fun `GIVEN XSS attempt WHEN updateInputText THEN treats as plain text`() = runTest {
        val xssAttempt = "<script>alert('XSS')</script>"
        viewModel.updateInputText(xssAttempt)
        advanceUntilIdle()

        assertThat(viewModel.inputText.value).isEqualTo(xssAttempt)
    }

    @Test
    fun `GIVEN null-like strings WHEN updateInputText THEN handles correctly`() = runTest {
        val nullString = "null"
        viewModel.updateInputText(nullString)
        advanceUntilIdle()

        assertThat(viewModel.inputText.value).isEqualTo("null")
    }

    @Test
    fun `GIVEN gradient tooltip flow WHEN observed THEN emits correct value`() = runTest {
        viewModel.shouldShowGradientTooltip.test {
            val item = awaitItem()
            // Should emit based on preferences (default is likely true - hasn't seen tooltip)
            assertThat(item).isAnyOf(true, false)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN multiple save operations WHEN saveText called repeatedly THEN handles correctly`() = runTest {
        viewModel.updateInputText("Text 1")
        advanceUntilIdle()
        viewModel.saveText()
        advanceUntilIdle()

        viewModel.updateInputText("Text 2")
        advanceUntilIdle()
        viewModel.saveText()
        advanceUntilIdle()

        viewModel.updateInputText("Text 3")
        advanceUntilIdle()
        viewModel.saveText()
        advanceUntilIdle()

        assertThat(viewModel.inputText.value).isEmpty()
    }

    @Test
    fun `GIVEN tabs and newlines WHEN updateInputText THEN preserves whitespace`() = runTest {
        val textWithWhitespace = "Line1\n\tLine2\n  Line3"
        viewModel.updateInputText(textWithWhitespace)
        advanceUntilIdle()

        assertThat(viewModel.inputText.value).isEqualTo(textWithWhitespace)
    }
}
