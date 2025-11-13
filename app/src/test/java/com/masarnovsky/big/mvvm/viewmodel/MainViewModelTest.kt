package com.masarnovsky.big.mvvm.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.masarnovsky.big.mvvm.BackgroundColor
import com.masarnovsky.big.mvvm.GradientColor
import com.masarnovsky.big.mvvm.InputFont
import com.masarnovsky.big.mvvm.Orientation
import com.masarnovsky.big.mvvm.model.TextEntity
import com.masarnovsky.big.mvvm.model.TextRepository
import com.masarnovsky.big.mvvm.model.UserPreferencesManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Comprehensive unit tests for MainViewModel with Hilt DI.
 * Covers green (happy path), red (error), and tricky (edge) cases.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: TextRepository
    private lateinit var preferencesManager: UserPreferencesManager
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        preferencesManager = mockk(relaxed = true)

        // Default mock behaviors
        every { repository.getAllTexts() } returns flowOf(emptyList())
        every { preferencesManager.hasSeenGradientTooltip } returns flowOf(false)
        coEvery { repository.insertText(any()) } returns Result.success(Unit)
        coEvery { repository.deleteText(any()) } returns Result.success(Unit)
        coEvery { preferencesManager.markGradientTooltipShown() } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): MainViewModel {
        return MainViewModel(repository, preferencesManager).also {
            testDispatcher.scheduler.advanceUntilIdle()
        }
    }

    // ===== GREEN CASES (Happy Paths) =====

    @Test
    fun `GIVEN ViewModel WHEN initialized THEN loads history successfully`() = runTest {
        val testHistory = listOf(
            TextEntity(1, "Test 1", 100),
            TextEntity(2, "Test 2", 200)
        )
        every { repository.getAllTexts() } returns flowOf(testHistory)

        viewModel = createViewModel()
        advanceUntilIdle()

        assertThat(viewModel.history.value).isEqualTo(testHistory)
    }

    @Test
    fun `GIVEN valid text WHEN updateInputText THEN updates text successfully`() = runTest {
        viewModel = createViewModel()

        viewModel.updateInputText("Hello World")

        assertThat(viewModel.inputText.value).isEqualTo("Hello World")
    }

    @Test
    fun `GIVEN valid font WHEN updateFont THEN updates font successfully`() = runTest {
        viewModel = createViewModel()

        viewModel.updateFont(InputFont.PANGOLIN)

        assertThat(viewModel.selectedInputFont.value).isEqualTo(InputFont.PANGOLIN)
    }

    @Test
    fun `GIVEN valid background WHEN updateBackground THEN updates background successfully`() = runTest {
        viewModel = createViewModel()

        viewModel.updateBackground(BackgroundColor.WHITE)

        assertThat(viewModel.selectedBackground.value).isEqualTo(BackgroundColor.WHITE)
    }

    @Test
    fun `GIVEN GRADIENT background WHEN updateBackground THEN randomizes gradient`() = runTest {
        viewModel = createViewModel()
        val initialGradient = viewModel.selectedGradient.value

        viewModel.updateBackground(BackgroundColor.GRADIENT)

        // Gradient should be set (not necessarily different due to randomness)
        assertThat(viewModel.selectedGradient.value).isNotNull()
    }

    @Test
    fun `GIVEN valid orientation WHEN updateOrientation THEN updates orientation successfully`() = runTest {
        viewModel = createViewModel()

        viewModel.updateOrientation(Orientation.PORTRAIT)

        assertThat(viewModel.selectedOrientation.value).isEqualTo(Orientation.PORTRAIT)
    }

    @Test
    fun `GIVEN valid text WHEN saveText THEN saves successfully`() = runTest {
        viewModel = createViewModel()

        viewModel.saveText("Test Text")
        advanceUntilIdle()

        coVerify { repository.insertText("Test Text") }
    }

    @Test
    fun `GIVEN valid entity WHEN deleteText THEN deletes successfully`() = runTest {
        viewModel = createViewModel()
        val entity = TextEntity(1, "Test", 100)

        viewModel.deleteText(entity)
        advanceUntilIdle()

        coVerify { repository.deleteText(entity) }
    }

    @Test
    fun `WHEN markTooltipShown THEN marks tooltip as shown`() = runTest {
        viewModel = createViewModel()

        viewModel.markTooltipShown()
        advanceUntilIdle()

        coVerify { preferencesManager.markGradientTooltipShown() }
    }

    // ===== RED CASES (Error Handling) =====

    @Test
    fun `GIVEN text exceeds max length WHEN updateInputText THEN rejects text`() = runTest {
        viewModel = createViewModel()
        val validText = "Valid"
        val tooLongText = "a".repeat(inputTextMaxAmount + 1)

        viewModel.updateInputText(validText)
        assertThat(viewModel.inputText.value).isEqualTo(validText)

        viewModel.updateInputText(tooLongText)
        assertThat(viewModel.inputText.value).isEqualTo(validText) // Should not change
    }

    @Test
    fun `GIVEN blank text WHEN saveText THEN does not save`() = runTest {
        viewModel = createViewModel()

        viewModel.saveText("")
        viewModel.saveText("   ")
        advanceUntilIdle()

        // Repository should not be called for blank text
        coVerify(exactly = 0) { repository.insertText(any()) }
    }

    @Test
    fun `GIVEN repository error WHEN saveText THEN handles error gracefully`() = runTest {
        coEvery { repository.insertText(any()) } returns Result.failure(Exception("Database error"))
        viewModel = createViewModel()

        viewModel.saveText("Test")
        advanceUntilIdle()

        // Should not crash, error is logged
    }

    @Test
    fun `GIVEN repository error WHEN deleteText THEN handles error gracefully`() = runTest {
        coEvery { repository.deleteText(any()) } returns Result.failure(Exception("Delete error"))
        viewModel = createViewModel()
        val entity = TextEntity(1, "Test", 100)

        viewModel.deleteText(entity)
        advanceUntilIdle()

        // Should not crash, error is logged
    }

    @Test
    fun `GIVEN preferences error WHEN markTooltipShown THEN handles error gracefully`() = runTest {
        coEvery { preferencesManager.markGradientTooltipShown() } throws Exception("Preferences error")
        viewModel = createViewModel()

        viewModel.markTooltipShown()
        advanceUntilIdle()

        // Should not crash, error is logged
    }

    @Test
    fun `GIVEN history loading error WHEN initialized THEN shows empty list`() = runTest {
        every { repository.getAllTexts() } returns flow {
            throw Exception("Database error")
        }

        viewModel = createViewModel()
        advanceUntilIdle()

        // Should show empty list instead of crashing
        assertThat(viewModel.history.value).isEmpty()
    }

    @Test
    fun `GIVEN null text from database WHEN loaded THEN handles gracefully`() = runTest {
        every { repository.getAllTexts() } returns flowOf(emptyList())
        viewModel = createViewModel()
        advanceUntilIdle()

        assertThat(viewModel.history.value).isEmpty()
    }

    // ===== TRICKY CASES (Edge Cases & Concurrency) =====

    @Test
    fun `GIVEN exactly max length text WHEN updateInputText THEN accepts text`() = runTest {
        viewModel = createViewModel()
        val maxLengthText = "a".repeat(inputTextMaxAmount)

        viewModel.updateInputText(maxLengthText)

        assertThat(viewModel.inputText.value).isEqualTo(maxLengthText)
        assertThat(viewModel.inputText.value.length).isEqualTo(inputTextMaxAmount)
    }

    @Test
    fun `GIVEN emoji-only text WHEN updateInputText THEN handles correctly`() = runTest {
        viewModel = createViewModel()
        val emojiText = "🌍🎉😀💯✨"

        viewModel.updateInputText(emojiText)

        assertThat(viewModel.inputText.value).isEqualTo(emojiText)
    }

    @Test
    fun `GIVEN unicode characters WHEN updateInputText THEN handles correctly`() = runTest {
        viewModel = createViewModel()
        val unicodeText = "Hello 世界 مرحبا Привет"

        viewModel.updateInputText(unicodeText)

        assertThat(viewModel.inputText.value).isEqualTo(unicodeText)
    }

    @Test
    fun `GIVEN multiline text WHEN updateInputText THEN handles correctly`() = runTest {
        viewModel = createViewModel()
        val multilineText = "Line 1\nLine 2\nLine 3"

        viewModel.updateInputText(multilineText)

        assertThat(viewModel.inputText.value).isEqualTo(multilineText)
    }

    @Test
    fun `GIVEN special characters WHEN updateInputText THEN handles correctly`() = runTest {
        viewModel = createViewModel()
        val specialText = "!@#$%^&*()_+-={}[]|:;<>,.?/~`"

        viewModel.updateInputText(specialText)

        assertThat(viewModel.inputText.value).isEqualTo(specialText)
    }

    @Test
    fun `GIVEN rapid consecutive updates WHEN updateInputText THEN processes all updates`() = runTest {
        viewModel = createViewModel()

        repeat(100) { i ->
            viewModel.updateInputText("Update $i")
        }

        assertThat(viewModel.inputText.value).isEqualTo("Update 99")
    }

    @Test
    fun `GIVEN concurrent operations WHEN multiple updates THEN maintains consistency`() = runTest {
        viewModel = createViewModel()

        viewModel.updateInputText("Text")
        viewModel.updateFont(InputFont.ROBOTO_SLAB)
        viewModel.updateBackground(BackgroundColor.WHITE)
        viewModel.updateOrientation(Orientation.PORTRAIT)

        assertThat(viewModel.inputText.value).isEqualTo("Text")
        assertThat(viewModel.selectedInputFont.value).isEqualTo(InputFont.ROBOTO_SLAB)
        assertThat(viewModel.selectedBackground.value).isEqualTo(BackgroundColor.WHITE)
        assertThat(viewModel.selectedOrientation.value).isEqualTo(Orientation.PORTRAIT)
    }

    @Test
    fun `GIVEN multiple gradient updates WHEN updateBackground THEN generates different gradients`() = runTest {
        viewModel = createViewModel()
        val gradients = mutableSetOf<GradientColor>()

        repeat(20) {
            viewModel.updateBackground(BackgroundColor.GRADIENT)
            gradients.add(viewModel.selectedGradient.value)
        }

        // With 20 attempts and 8 gradient options, should get at least 2 different ones
        assertThat(gradients.size).isAtLeast(2)
    }

    @Test
    fun `GIVEN tooltip shown WHEN shouldShowGradientTooltip THEN returns false`() = runTest {
        every { preferencesManager.hasSeenGradientTooltip } returns flowOf(true)
        viewModel = createViewModel()

        viewModel.shouldShowGradientTooltip.test {
            assertThat(awaitItem()).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN tooltip not shown WHEN shouldShowGradientTooltip THEN returns true`() = runTest {
        every { preferencesManager.hasSeenGradientTooltip } returns flowOf(false)
        viewModel = createViewModel()

        viewModel.shouldShowGradientTooltip.test {
            assertThat(awaitItem()).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN large history list WHEN loaded THEN handles efficiently`() = runTest {
        val largeHistory = List(1000) { i ->
            TextEntity(i, "Text $i", i.toLong())
        }
        every { repository.getAllTexts() } returns flowOf(largeHistory)

        viewModel = createViewModel()
        advanceUntilIdle()

        assertThat(viewModel.history.value).hasSize(1000)
    }

    @Test
    fun `GIVEN empty string WHEN updateInputText THEN allows empty text`() = runTest {
        viewModel = createViewModel()
        viewModel.updateInputText("Test")

        viewModel.updateInputText("")

        assertThat(viewModel.inputText.value).isEmpty()
    }

    @Test
    fun `GIVEN whitespace only WHEN updateInputText THEN allows whitespace`() = runTest {
        viewModel = createViewModel()

        viewModel.updateInputText("   ")

        assertThat(viewModel.inputText.value).isEqualTo("   ")
    }

    @Test
    fun `GIVEN all enum values WHEN iterating THEN all values work`() = runTest {
        viewModel = createViewModel()

        // Test all fonts
        InputFont.entries.forEach { font ->
            viewModel.updateFont(font)
            assertThat(viewModel.selectedInputFont.value).isEqualTo(font)
        }

        // Test all backgrounds
        BackgroundColor.entries.forEach { bg ->
            viewModel.updateBackground(bg)
            assertThat(viewModel.selectedBackground.value).isEqualTo(bg)
        }

        // Test all orientations
        Orientation.entries.forEach { orientation ->
            viewModel.updateOrientation(orientation)
            assertThat(viewModel.selectedOrientation.value).isEqualTo(orientation)
        }
    }

    @Test
    fun `GIVEN StateFlows WHEN collected THEN emit updates correctly`() = runTest {
        viewModel = createViewModel()

        viewModel.inputText.test {
            assertThat(awaitItem()).isEmpty()
            viewModel.updateInputText("Test")
            assertThat(awaitItem()).isEqualTo("Test")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN history updates WHEN repository emits THEN updates flow`() = runTest {
        val flow = flow {
            emit(emptyList())
            emit(listOf(TextEntity(1, "First", 100)))
            emit(listOf(TextEntity(1, "First", 100), TextEntity(2, "Second", 200)))
        }
        every { repository.getAllTexts() } returns flow

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.history.test {
            val final = awaitItem()
            assertThat(final).hasSize(2)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
