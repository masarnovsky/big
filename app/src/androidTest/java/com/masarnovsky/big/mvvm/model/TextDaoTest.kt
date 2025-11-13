package com.masarnovsky.big.mvvm.model

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for TextDao and Room database operations.
 * These tests run on an Android device or emulator.
 */
@RunWith(AndroidJUnit4::class)
class TextDaoTest {

    private lateinit var database: TextDatabase
    private lateinit var dao: TextDao

    @Before
    fun setup() {
        // Create an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TextDatabase::class.java
        ).allowMainThreadQueries() // Only for testing
            .build()

        dao = database.textDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    // Insert Tests

    @Test
    fun insertText_shouldAddToDatabase() = runTest {
        val textEntity = TextEntity(text = "Test text")

        dao.insert(textEntity)

        dao.getAllTexts().test {
            val items = awaitItem()
            assertThat(items).hasSize(1)
            assertThat(items[0].text).isEqualTo("Test text")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertMultipleTexts_shouldAddAll() = runTest {
        val text1 = TextEntity(text = "First")
        val text2 = TextEntity(text = "Second")
        val text3 = TextEntity(text = "Third")

        dao.insert(text1)
        dao.insert(text2)
        dao.insert(text3)

        dao.getAllTexts().test {
            val items = awaitItem()
            assertThat(items).hasSize(3)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertText_shouldAutoGenerateId() = runTest {
        val textEntity1 = TextEntity(text = "First")
        val textEntity2 = TextEntity(text = "Second")

        dao.insert(textEntity1)
        dao.insert(textEntity2)

        dao.getAllTexts().test {
            val items = awaitItem()
            assertThat(items[0].id).isNotEqualTo(0)
            assertThat(items[1].id).isNotEqualTo(0)
            assertThat(items[0].id).isNotEqualTo(items[1].id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertText_shouldAutoGenerateTimestamp() = runTest {
        val textEntity = TextEntity(text = "Test")

        dao.insert(textEntity)

        dao.getAllTexts().test {
            val items = awaitItem()
            assertThat(items[0].timestamp).isGreaterThan(0L)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Query Tests

    @Test
    fun getAllTexts_whenEmpty_shouldReturnEmptyList() = runTest {
        dao.getAllTexts().test {
            val items = awaitItem()
            assertThat(items).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllTexts_shouldReturnInDescendingTimestampOrder() = runTest {
        // Insert with small delays to ensure different timestamps
        val first = TextEntity(text = "First", timestamp = 1000)
        val second = TextEntity(text = "Second", timestamp = 2000)
        val third = TextEntity(text = "Third", timestamp = 3000)

        dao.insert(first)
        Thread.sleep(10)
        dao.insert(second)
        Thread.sleep(10)
        dao.insert(third)

        dao.getAllTexts().test {
            val items = awaitItem()
            assertThat(items).hasSize(3)
            // Most recent first
            assertThat(items[0].text).isEqualTo("Third")
            assertThat(items[1].text).isEqualTo("Second")
            assertThat(items[2].text).isEqualTo("First")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllTexts_shouldEmitUpdates() = runTest {
        dao.getAllTexts().test {
            // Initial empty state
            assertThat(awaitItem()).isEmpty()

            // Insert first item
            dao.insert(TextEntity(text = "First"))
            assertThat(awaitItem()).hasSize(1)

            // Insert second item
            dao.insert(TextEntity(text = "Second"))
            assertThat(awaitItem()).hasSize(2)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // Delete Tests

    @Test
    fun deleteText_shouldRemoveFromDatabase() = runTest {
        val textEntity = TextEntity(id = 1, text = "Test")

        dao.insert(textEntity)

        dao.getAllTexts().test {
            val items = awaitItem()
            assertThat(items).hasSize(1)

            dao.delete(items[0])

            val updatedItems = awaitItem()
            assertThat(updatedItems).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteText_shouldOnlyDeleteSpecifiedItem() = runTest {
        val text1 = TextEntity(text = "First")
        val text2 = TextEntity(text = "Second")
        val text3 = TextEntity(text = "Third")

        dao.insert(text1)
        dao.insert(text2)
        dao.insert(text3)

        dao.getAllTexts().test {
            val items = awaitItem()
            assertThat(items).hasSize(3)

            // Delete the middle item
            dao.delete(items[1])

            val updatedItems = awaitItem()
            assertThat(updatedItems).hasSize(2)
            assertThat(updatedItems.map { it.text }).containsExactly("Third", "First")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteNonExistentText_shouldNotCrash() = runTest {
        val textEntity = TextEntity(id = 999, text = "Non-existent")

        // Should not throw an exception
        dao.delete(textEntity)

        dao.getAllTexts().test {
            assertThat(awaitItem()).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Edge Cases

    @Test
    fun insertTextWithSpecialCharacters() = runTest {
        val specialText = "!@#$%^&*()_+-={}[]|:;<>,.?/~`"
        val textEntity = TextEntity(text = specialText)

        dao.insert(textEntity)

        dao.getAllTexts().test {
            val items = awaitItem()
            assertThat(items[0].text).isEqualTo(specialText)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertTextWithUnicodeCharacters() = runTest {
        val unicodeText = "Hello 世界 🌍 مرحبا"
        val textEntity = TextEntity(text = unicodeText)

        dao.insert(textEntity)

        dao.getAllTexts().test {
            val items = awaitItem()
            assertThat(items[0].text).isEqualTo(unicodeText)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertTextWithNewlines() = runTest {
        val multilineText = "Line 1\nLine 2\nLine 3"
        val textEntity = TextEntity(text = multilineText)

        dao.insert(textEntity)

        dao.getAllTexts().test {
            val items = awaitItem()
            assertThat(items[0].text).isEqualTo(multilineText)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertVeryLongText() = runTest {
        val longText = "a".repeat(1000)
        val textEntity = TextEntity(text = longText)

        dao.insert(textEntity)

        dao.getAllTexts().test {
            val items = awaitItem()
            assertThat(items[0].text).hasLength(1000)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertEmptyText() = runTest {
        val textEntity = TextEntity(text = "")

        dao.insert(textEntity)

        dao.getAllTexts().test {
            val items = awaitItem()
            assertThat(items[0].text).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Stress Tests

    @Test
    fun insertManyTexts_shouldHandleCorrectly() = runTest {
        repeat(100) { i ->
            dao.insert(TextEntity(text = "Text $i"))
        }

        dao.getAllTexts().test {
            val items = awaitItem()
            assertThat(items).hasSize(100)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun rapidInsertAndDelete_shouldMaintainConsistency() = runTest {
        dao.getAllTexts().test {
            assertThat(awaitItem()).isEmpty()

            // Rapid inserts
            repeat(10) { i ->
                dao.insert(TextEntity(text = "Text $i"))
                val items = awaitItem()
                assertThat(items).hasSize(i + 1)
            }

            // Rapid deletes
            val currentItems = expectMostRecentItem()
            currentItems.take(5).forEach { item ->
                dao.delete(item)
                awaitItem() // Wait for update
            }

            val finalItems = expectMostRecentItem()
            assertThat(finalItems).hasSize(5)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
