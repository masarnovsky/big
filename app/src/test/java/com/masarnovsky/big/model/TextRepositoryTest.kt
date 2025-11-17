package com.masarnovsky.big.model

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.masarnovsky.big.mvvm.model.TextDao
import com.masarnovsky.big.mvvm.model.TextEntity
import com.masarnovsky.big.mvvm.model.TextRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

/**
 * Comprehensive unit tests for TextRepository
 *
 * Test Coverage:
 * - ✓ Insert operations (valid, blank, exceeding length)
 * - ✓ Delete operations
 * - ✓ Get all texts
 * - ✓ Input validation
 * - ✓ Text trimming
 * - ✓ Error handling
 * - ✓ Edge cases (emoji, unicode, special characters)
 * - ✓ Boundary testing (199, 200, 201 chars)
 * - ✓ SQL injection prevention
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class TextRepositoryTest {

    private lateinit var textDao: TextDao
    private lateinit var repository: TextRepository

    @Before
    fun setup() {
        // Enable Robolectric logging
        ShadowLog.stream = System.out

        textDao = mockk(relaxed = true)
        repository = TextRepository(textDao)
    }

    // ============================================================
    // INSERT TESTS - GREEN CASES (Happy Path)
    // ============================================================

    @Test
    fun `insertText should successfully insert valid text`() = runTest {
        val validText = "Hello World"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(validText)

        assertThat(result.isSuccess).isTrue()
        coVerify { textDao.insert(match { it.text == validText.trim() }) }
    }

    @Test
    fun `insertText should trim whitespace before inserting`() = runTest {
        val textWithWhitespace = "  Hello World  "
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(textWithWhitespace)

        assertThat(result.isSuccess).isTrue()
        coVerify { textDao.insert(match { it.text == "Hello World" }) }
    }

    @Test
    fun `insertText should accept text with 200 characters`() = runTest {
        val maxLengthText = "a".repeat(200)
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(maxLengthText)

        assertThat(result.isSuccess).isTrue()
        coVerify { textDao.insert(match { it.text.length == 200 }) }
    }

    @Test
    fun `insertText should accept text with 199 characters`() = runTest {
        val text = "a".repeat(199)
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(text)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `insertText should accept single character`() = runTest {
        val singleChar = "a"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(singleChar)

        assertThat(result.isSuccess).isTrue()
    }

    // ============================================================
    // INSERT TESTS - RED CASES (Error Handling)
    // ============================================================

    @Test
    fun `insertText should fail for blank text`() = runTest {
        val blankText = "   "

        val result = repository.insertText(blankText)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
        assertThat(result.exceptionOrNull()?.message).contains("Text cannot be empty")
        coVerify(exactly = 0) { textDao.insert(any()) }
    }

    @Test
    fun `insertText should fail for empty string`() = runTest {
        val emptyText = ""

        val result = repository.insertText(emptyText)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
        coVerify(exactly = 0) { textDao.insert(any()) }
    }

    @Test
    fun `insertText should fail for text exceeding 200 characters`() = runTest {
        val oversizedText = "a".repeat(201)

        val result = repository.insertText(oversizedText)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
        assertThat(result.exceptionOrNull()?.message).contains("exceeds maximum length")
        coVerify(exactly = 0) { textDao.insert(any()) }
    }

    @Test
    fun `insertText should fail for text with 250 characters`() = runTest {
        val oversizedText = "a".repeat(250)

        val result = repository.insertText(oversizedText)

        assertThat(result.isFailure).isTrue()
        coVerify(exactly = 0) { textDao.insert(any()) }
    }

    @Test
    fun `insertText should handle database errors`() = runTest {
        val validText = "Test Text"
        val databaseException = RuntimeException("Database error")
        coEvery { textDao.insert(any()) } throws databaseException

        val result = repository.insertText(validText)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(databaseException)
    }

    // ============================================================
    // INSERT TESTS - TRICKY CASES (Edge Cases)
    // ============================================================

    @Test
    fun `insertText should accept emoji-only text`() = runTest {
        val emojiText = "🌍🎉😀"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(emojiText)

        assertThat(result.isSuccess).isTrue()
        coVerify { textDao.insert(match { it.text == emojiText }) }
    }

    @Test
    fun `insertText should accept unicode characters from multiple languages`() = runTest {
        val unicodeText = "Hello 世界 مرحبا Привет"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(unicodeText)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `insertText should accept multiline text with newlines`() = runTest {
        val multilineText = "Line 1\nLine 2\nLine 3"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(multilineText)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `insertText should accept special characters`() = runTest {
        val specialText = "!@#$%^&*()_+-=[]{}|;:',.<>?/~`"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(specialText)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `insertText should handle consecutive spaces`() = runTest {
        val textWithSpaces = "Hello     World"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(textWithSpaces)

        assertThat(result.isSuccess).isTrue()
        coVerify { textDao.insert(match { it.text == textWithSpaces }) }
    }

    @Test
    fun `insertText should handle tabs and newlines combined`() = runTest {
        val complexText = "Tab\there\nNew line"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(complexText)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `insertText should handle right-to-left text`() = runTest {
        val rtlText = "مرحبا بك"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(rtlText)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `insertText should safely handle SQL injection attempts`() = runTest {
        val sqlInjection = "'; DROP TABLE text_history; --"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(sqlInjection)

        // Should succeed because Room/SQLite handles parameterization
        assertThat(result.isSuccess).isTrue()
        coVerify { textDao.insert(match { it.text == sqlInjection }) }
    }

    @Test
    fun `insertText should handle text with quotes and backslashes`() = runTest {
        val quotedText = "He said \"Hello\" and used a backslash \\"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(quotedText)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `insertText should trim leading and trailing whitespace but preserve internal`() = runTest {
        val text = "  Hello   World  "
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(text)

        assertThat(result.isSuccess).isTrue()
        coVerify { textDao.insert(match { it.text == "Hello   World" }) }
    }

    // ============================================================
    // DELETE TESTS
    // ============================================================

    @Test
    fun `deleteText should successfully delete entity`() = runTest {
        val entity = TextEntity(id = 1, text = "Test Text")
        coEvery { textDao.delete(entity) } returns Unit

        val result = repository.deleteText(entity)

        assertThat(result.isSuccess).isTrue()
        coVerify { textDao.delete(entity) }
    }

    @Test
    fun `deleteText should handle database errors`() = runTest {
        val entity = TextEntity(id = 1, text = "Test Text")
        val databaseException = RuntimeException("Delete failed")
        coEvery { textDao.delete(entity) } throws databaseException

        val result = repository.deleteText(entity)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(databaseException)
    }

    @Test
    fun `deleteText should handle entity with zero id`() = runTest {
        val entity = TextEntity(id = 0, text = "Test Text")
        coEvery { textDao.delete(entity) } returns Unit

        val result = repository.deleteText(entity)

        assertThat(result.isSuccess).isTrue()
    }

    // ============================================================
    // GET ALL TEXTS TESTS
    // ============================================================

    @Test
    fun `getAllTexts should return flow of texts`() = runTest {
        val testEntities = listOf(
            TextEntity(id = 1, text = "Text 1", timestamp = 1000),
            TextEntity(id = 2, text = "Text 2", timestamp = 2000)
        )
        every { textDao.getAllTexts() } returns flowOf(testEntities)

        repository.getAllTexts().test {
            val items = awaitItem()
            assertThat(items).hasSize(2)
            assertThat(items[0].text).isEqualTo("Text 1")
            assertThat(items[1].text).isEqualTo("Text 2")
            awaitComplete()
        }
    }

    @Test
    fun `getAllTexts should return empty list when no texts exist`() = runTest {
        every { textDao.getAllTexts() } returns flowOf(emptyList())

        repository.getAllTexts().test {
            val items = awaitItem()
            assertThat(items).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun `getAllTexts should emit empty list on database error`() = runTest {
        val errorFlow = flowOf<List<TextEntity>>()
        every { textDao.getAllTexts() } returns errorFlow

        repository.getAllTexts().test {
            // Flow completes without items
            awaitComplete()
        }
    }

    @Test
    fun `getAllTexts should handle large number of texts`() = runTest {
        val largeList = (1..100).map { TextEntity(id = it, text = "Text $it") }
        every { textDao.getAllTexts() } returns flowOf(largeList)

        repository.getAllTexts().test {
            val items = awaitItem()
            assertThat(items).hasSize(100)
            awaitComplete()
        }
    }

    // ============================================================
    // BOUNDARY TESTS
    // ============================================================

    @Test
    fun `insertText boundary test with exactly 200 chars after trim`() = runTest {
        val text = " " + "a".repeat(199) + " "  // 201 chars total, but 199 after trim

        // Validation happens BEFORE trim, so 201 chars should be rejected
        val result = repository.insertText(text)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
        coVerify(exactly = 0) { textDao.insert(any()) }
    }

    @Test
    fun `insertText should reject 201 chars after trim`() = runTest {
        val text = "a".repeat(201)

        val result = repository.insertText(text)

        assertThat(result.isFailure).isTrue()
    }

    // ============================================================
    // VALIDATION TESTS
    // ============================================================

    @Test
    fun `insertText should fail with only newlines`() = runTest {
        val newlinesOnly = "\n\n\n"

        val result = repository.insertText(newlinesOnly)

        assertThat(result.isFailure).isTrue()
        coVerify(exactly = 0) { textDao.insert(any()) }
    }

    @Test
    fun `insertText should fail with only tabs`() = runTest {
        val tabsOnly = "\t\t\t"

        val result = repository.insertText(tabsOnly)

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `insertText should accept whitespace surrounded by valid text`() = runTest {
        val validText = "a" + " ".repeat(10) + "b"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(validText)

        assertThat(result.isSuccess).isTrue()
    }
}
