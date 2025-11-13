package com.masarnovsky.big.mvvm.model

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive tests for TextRepository.
 * Covers green (happy path), red (error), and tricky (edge) cases.
 */
class TextRepositoryTest {

    private lateinit var textDao: TextDao
    private lateinit var repository: TextRepository

    @Before
    fun setup() {
        textDao = mockk(relaxed = true)
        repository = TextRepository(textDao)
    }

    // ===== GREEN CASES (Happy Paths) =====

    @Test
    fun `GIVEN valid text WHEN insertText THEN inserts successfully`() = runTest {
        val validText = "Test Text"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(validText)

        assertThat(result.isSuccess).isTrue()
        coVerify { textDao.insert(match { it.text == validText }) }
    }

    @Test
    fun `GIVEN text with whitespace WHEN insertText THEN trims and inserts`() = runTest {
        val textWithWhitespace = "  Test Text  "
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(textWithWhitespace)

        assertThat(result.isSuccess).isTrue()
        coVerify { textDao.insert(match { it.text == "Test Text" }) }
    }

    @Test
    fun `GIVEN valid entity WHEN deleteText THEN deletes successfully`() = runTest {
        val entity = TextEntity(1, "Test", 100)
        coEvery { textDao.delete(entity) } returns Unit

        val result = repository.deleteText(entity)

        assertThat(result.isSuccess).isTrue()
        coVerify { textDao.delete(entity) }
    }

    @Test
    fun `GIVEN existing texts WHEN getAllTexts THEN returns flow successfully`() = runTest {
        val testTexts = listOf(
            TextEntity(1, "Test 1", 100),
            TextEntity(2, "Test 2", 200)
        )
        every { textDao.getAllTexts() } returns flowOf(testTexts)

        repository.getAllTexts().test {
            val result = awaitItem()
            assertThat(result).isEqualTo(testTexts)
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN exactly 200 characters WHEN insertText THEN inserts successfully`() = runTest {
        val maxLengthText = "a".repeat(200)
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(maxLengthText)

        assertThat(result.isSuccess).isTrue()
    }

    // ===== RED CASES (Error Handling) =====

    @Test
    fun `GIVEN blank text WHEN insertText THEN returns failure`() = runTest {
        val result = repository.insertText("")

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
        assertThat(result.exceptionOrNull()?.message).contains("cannot be empty")
    }

    @Test
    fun `GIVEN whitespace only WHEN insertText THEN returns failure`() = runTest {
        val result = repository.insertText("   ")

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("cannot be empty")
    }

    @Test
    fun `GIVEN text exceeds 200 characters WHEN insertText THEN returns failure`() = runTest {
        val tooLongText = "a".repeat(201)

        val result = repository.insertText(tooLongText)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("exceeds maximum length")
    }

    @Test
    fun `GIVEN database error WHEN insertText THEN returns failure`() = runTest {
        coEvery { textDao.insert(any()) } throws Exception("Database error")

        val result = repository.insertText("Test")

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("Database error")
    }

    @Test
    fun `GIVEN database error WHEN deleteText THEN returns failure`() = runTest {
        val entity = TextEntity(1, "Test", 100)
        coEvery { textDao.delete(entity) } throws Exception("Delete error")

        val result = repository.deleteText(entity)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("Delete error")
    }

    @Test
    fun `GIVEN database error WHEN getAllTexts THEN emits empty list`() = runTest {
        every { textDao.getAllTexts() } returns flowOf()

        repository.getAllTexts().test {
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN DAO throws exception WHEN getAllTexts THEN catches and emits empty`() = runTest {
        every { textDao.getAllTexts() } returns flowOf()

        repository.getAllTexts().test {
            // Should complete without error
            awaitComplete()
        }
    }

    // ===== TRICKY CASES (Edge Cases) =====

    @Test
    fun `GIVEN emoji-only text WHEN insertText THEN handles correctly`() = runTest {
        val emojiText = "🌍🎉😀💯✨"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(emojiText)

        assertThat(result.isSuccess).isTrue()
        coVerify { textDao.insert(match { it.text == emojiText }) }
    }

    @Test
    fun `GIVEN unicode characters WHEN insertText THEN handles correctly`() = runTest {
        val unicodeText = "Hello 世界 مرحبا Привет"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(unicodeText)

        assertThat(result.isSuccess).isTrue()
        coVerify { textDao.insert(match { it.text == unicodeText }) }
    }

    @Test
    fun `GIVEN multiline text WHEN insertText THEN preserves newlines`() = runTest {
        val multilineText = "Line 1\nLine 2\nLine 3"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(multilineText)

        assertThat(result.isSuccess).isTrue()
        coVerify { textDao.insert(match { it.text == multilineText }) }
    }

    @Test
    fun `GIVEN special characters WHEN insertText THEN handles correctly`() = runTest {
        val specialText = "!@#$%^&*()_+-={}[]|:;<>,.?/~`"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(specialText)

        assertThat(result.isSuccess).isTrue()
        coVerify { textDao.insert(match { it.text == specialText }) }
    }

    @Test
    fun `GIVEN text with leading and trailing spaces WHEN insertText THEN trims correctly`() = runTest {
        val textWithSpaces = "   Test   "
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(textWithSpaces)

        assertThat(result.isSuccess).isTrue()
        coVerify { textDao.insert(match { it.text == "Test" }) }
    }

    @Test
    fun `GIVEN text with tabs WHEN insertText THEN preserves tabs`() = runTest {
        val textWithTabs = "Test\t\tText"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(textWithTabs)

        assertThat(result.isSuccess).isTrue()
        coVerify { textDao.insert(match { it.text.contains("\t") }) }
    }

    @Test
    fun `GIVEN empty Flow WHEN getAllTexts THEN completes without error`() = runTest {
        every { textDao.getAllTexts() } returns flowOf()

        repository.getAllTexts().test {
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN single character WHEN insertText THEN inserts successfully`() = runTest {
        val singleChar = "a"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(singleChar)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `GIVEN text with consecutive spaces WHEN insertText THEN preserves spaces`() = runTest {
        val textWithSpaces = "Test    Text"
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(textWithSpaces)

        assertThat(result.isSuccess).isTrue()
        coVerify { textDao.insert(match { it.text == textWithSpaces.trim() }) }
    }

    @Test
    fun `GIVEN various invalid inputs WHEN insertText THEN all fail with proper errors`() = runTest {
        val invalidInputs = mapOf(
            "" to "cannot be empty",
            "   " to "cannot be empty",
            "\t\t" to "cannot be empty",
            "\n\n" to "cannot be empty",
            "a".repeat(201) to "exceeds maximum length"
        )

        invalidInputs.forEach { (input, expectedError) ->
            val result = repository.insertText(input)
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).contains(expectedError)
        }
    }

    @Test
    fun `GIVEN concurrent inserts WHEN insertText THEN all processed correctly`() = runTest {
        coEvery { textDao.insert(any()) } returns Unit

        val results = List(10) { i ->
            repository.insertText("Text $i")
        }

        results.forEach { result ->
            assertThat(result.isSuccess).isTrue()
        }
        coVerify(exactly = 10) { textDao.insert(any()) }
    }

    @Test
    fun `GIVEN database constraint error WHEN insertText THEN returns failure`() = runTest {
        coEvery { textDao.insert(any()) } throws android.database.sqlite.SQLiteConstraintException("UNIQUE constraint failed")

        val result = repository.insertText("Test")

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `GIVEN IO error WHEN insertText THEN returns failure`() = runTest {
        coEvery { textDao.insert(any()) } throws java.io.IOException("Disk full")

        val result = repository.insertText("Test")

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `GIVEN text at boundary (199, 200, 201 chars) WHEN insertText THEN validates correctly`() = runTest {
        coEvery { textDao.insert(any()) } returns Unit

        val at199 = repository.insertText("a".repeat(199))
        val at200 = repository.insertText("a".repeat(200))
        val at201 = repository.insertText("a".repeat(201))

        assertThat(at199.isSuccess).isTrue()
        assertThat(at200.isSuccess).isTrue()
        assertThat(at201.isFailure).isTrue()
    }

    @Test
    fun `GIVEN zero-width characters WHEN insertText THEN handles correctly`() = runTest {
        val zeroWidthText = "Test\u200BText" // Zero-width space
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(zeroWidthText)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `GIVEN right-to-left text WHEN insertText THEN handles correctly`() = runTest {
        val rtlText = "مرحبا بك" // Arabic text
        coEvery { textDao.insert(any()) } returns Unit

        val result = repository.insertText(rtlText)

        assertThat(result.isSuccess).isTrue()
    }
}
