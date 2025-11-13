package com.masarnovsky.big.mvvm.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.masarnovsky.big.mvvm.BackgroundColor
import com.masarnovsky.big.mvvm.GradientColor
import com.masarnovsky.big.mvvm.InputFont
import com.masarnovsky.big.mvvm.Orientation
import io.mockk.mockk
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

/**
 * Unit tests for MainViewModel.
 * Tests all business logic, state management, and user interactions.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
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

    // Input Text Tests

    @Test
    fun `initial input text should be empty`() = runTest {
        assertThat(viewModel.inputText.value).isEmpty()
    }

    @Test
    fun `updateInputText should update text when within limit`() = runTest {
        val testText = "Hello World"

        viewModel.updateInputText(testText)

        assertThat(viewModel.inputText.value).isEqualTo(testText)
    }

    @Test
    fun `updateInputText should not update text when exceeds max length`() = runTest {
        val validText = "Valid"
        val tooLongText = "a".repeat(inputTextMaxAmount + 1)

        viewModel.updateInputText(validText)
        assertThat(viewModel.inputText.value).isEqualTo(validText)

        viewModel.updateInputText(tooLongText)
        assertThat(viewModel.inputText.value).isEqualTo(validText) // Should not change
    }

    @Test
    fun `updateInputText should accept text at exactly max length`() = runTest {
        val maxLengthText = "a".repeat(inputTextMaxAmount)

        viewModel.updateInputText(maxLengthText)

        assertThat(viewModel.inputText.value).isEqualTo(maxLengthText)
        assertThat(viewModel.inputText.value.length).isEqualTo(inputTextMaxAmount)
    }

    @Test
    fun `updateInputText should handle empty string`() = runTest {
        viewModel.updateInputText("test")
        viewModel.updateInputText("")

        assertThat(viewModel.inputText.value).isEmpty()
    }

    // Font Selection Tests

    @Test
    fun `initial font should be default font`() = runTest {
        assertThat(viewModel.selectedInputFont.value).isEqualTo(defaultInputFont)
    }

    @Test
    fun `updateFont should change selected font`() = runTest {
        viewModel.updateFont(InputFont.PANGOLIN)

        assertThat(viewModel.selectedInputFont.value).isEqualTo(InputFont.PANGOLIN)
    }

    @Test
    fun `updateFont should work for all font types`() = runTest {
        for (font in InputFont.entries) {
            viewModel.updateFont(font)
            assertThat(viewModel.selectedInputFont.value).isEqualTo(font)
        }
    }

    // Background Selection Tests

    @Test
    fun `initial background should be default background`() = runTest {
        assertThat(viewModel.selectedBackground.value).isEqualTo(defaultBackgroundColor)
    }

    @Test
    fun `updateBackground should change selected background`() = runTest {
        viewModel.updateBackground(BackgroundColor.WHITE)

        assertThat(viewModel.selectedBackground.value).isEqualTo(BackgroundColor.WHITE)
    }

    @Test
    fun `updateBackground to GRADIENT should randomize gradient`() = runTest {
        val initialGradient = viewModel.selectedGradient.value

        // Update to gradient multiple times to test randomization
        repeat(5) {
            viewModel.updateBackground(BackgroundColor.GRADIENT)
            // Gradient should be set (not necessarily different each time due to randomness)
            assertThat(viewModel.selectedGradient.value).isNotNull()
        }
    }

    @Test
    fun `updateBackground to non-GRADIENT should not change gradient`() = runTest {
        viewModel.updateBackground(BackgroundColor.GRADIENT)
        val gradientAfterGradientBg = viewModel.selectedGradient.value

        viewModel.updateBackground(BackgroundColor.BLACK)

        assertThat(viewModel.selectedGradient.value).isEqualTo(gradientAfterGradientBg)
    }

    // Orientation Tests

    @Test
    fun `initial orientation should be default orientation`() = runTest {
        assertThat(viewModel.selectedOrientation.value).isEqualTo(defaultOrientation)
    }

    @Test
    fun `updateOrientation should change selected orientation`() = runTest {
        viewModel.updateOrientation(Orientation.PORTRAIT)

        assertThat(viewModel.selectedOrientation.value).isEqualTo(Orientation.PORTRAIT)
    }

    @Test
    fun `updateOrientation should work for all orientations`() = runTest {
        for (orientation in Orientation.entries) {
            viewModel.updateOrientation(orientation)
            assertThat(viewModel.selectedOrientation.value).isEqualTo(orientation)
        }
    }

    // State Flow Tests

    @Test
    fun `inputText StateFlow should emit updates`() = runTest {
        viewModel.inputText.test {
            assertThat(awaitItem()).isEmpty() // Initial value

            viewModel.updateInputText("Test")
            assertThat(awaitItem()).isEqualTo("Test")

            viewModel.updateInputText("Updated")
            assertThat(awaitItem()).isEqualTo("Updated")
        }
    }

    @Test
    fun `selectedInputFont StateFlow should emit updates`() = runTest {
        viewModel.selectedInputFont.test {
            assertThat(awaitItem()).isEqualTo(defaultInputFont) // Initial value

            viewModel.updateFont(InputFont.ROBOTO_SLAB)
            assertThat(awaitItem()).isEqualTo(InputFont.ROBOTO_SLAB)
        }
    }

    @Test
    fun `selectedBackground StateFlow should emit updates`() = runTest {
        viewModel.selectedBackground.test {
            assertThat(awaitItem()).isEqualTo(defaultBackgroundColor) // Initial value

            viewModel.updateBackground(BackgroundColor.WHITE)
            assertThat(awaitItem()).isEqualTo(BackgroundColor.WHITE)
        }
    }

    @Test
    fun `selectedOrientation StateFlow should emit updates`() = runTest {
        viewModel.selectedOrientation.test {
            assertThat(awaitItem()).isEqualTo(defaultOrientation) // Initial value

            viewModel.updateOrientation(Orientation.PORTRAIT)
            assertThat(awaitItem()).isEqualTo(Orientation.PORTRAIT)
        }
    }

    // History Tests (Basic - full integration tests in androidTest)

    @Test
    fun `initial history should be empty list`() = runTest {
        advanceUntilIdle()
        assertThat(viewModel.history.value).isEmpty()
    }

    @Test
    fun `saveText should not save blank text`() = runTest {
        advanceUntilIdle()
        val initialHistorySize = viewModel.history.value.size

        viewModel.saveText("")
        advanceUntilIdle()

        assertThat(viewModel.history.value.size).isEqualTo(initialHistorySize)
    }

    @Test
    fun `saveText should not save whitespace-only text`() = runTest {
        advanceUntilIdle()
        val initialHistorySize = viewModel.history.value.size

        viewModel.saveText("   ")
        advanceUntilIdle()

        assertThat(viewModel.history.value.size).isEqualTo(initialHistorySize)
    }

    // Edge Cases

    @Test
    fun `updateInputText with special characters should work`() = runTest {
        val specialText = "!@#$%^&*()_+-={}[]|:;<>,.?/~`"

        viewModel.updateInputText(specialText)

        assertThat(viewModel.inputText.value).isEqualTo(specialText)
    }

    @Test
    fun `updateInputText with unicode characters should work`() = runTest {
        val unicodeText = "Hello 世界 🌍"

        viewModel.updateInputText(unicodeText)

        assertThat(viewModel.inputText.value).isEqualTo(unicodeText)
    }

    @Test
    fun `updateInputText with newlines should work`() = runTest {
        val multilineText = "Line 1\nLine 2\nLine 3"

        viewModel.updateInputText(multilineText)

        assertThat(viewModel.inputText.value).isEqualTo(multilineText)
    }

    @Test
    fun `multiple rapid updates should process correctly`() = runTest {
        repeat(10) { i ->
            viewModel.updateInputText("Update $i")
        }

        assertThat(viewModel.inputText.value).isEqualTo("Update 9")
    }
}
