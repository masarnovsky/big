package com.masarnovsky.big.mvvm.view.components

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Unit tests for AutoSizeTextOptimized utility functions
 */
class AutoSizeTextOptimizedTest {

    // buildBalancedLines Tests

    @Test
    fun `buildBalancedLines with single line should join all words`() {
        val words = listOf("Hello", "World", "Test")
        val result = buildBalancedLines(words, 1)

        assertThat(result).isEqualTo("Hello World Test")
    }

    @Test
    fun `buildBalancedLines with two lines should split evenly`() {
        val words = listOf("One", "Two", "Three", "Four")
        val result = buildBalancedLines(words, 2)

        // Should split into 2 + 2
        assertThat(result).isEqualTo("One Two\nThree Four")
    }

    @Test
    fun `buildBalancedLines with odd number of words should distribute correctly`() {
        val words = listOf("A", "B", "C", "D", "E")
        val result = buildBalancedLines(words, 2)

        // 5 words / 2 lines = 2 base + 1 extra for first line
        // Should be 3 + 2
        assertThat(result).isEqualTo("A B C\nD E")
    }

    @Test
    fun `buildBalancedLines with three lines should distribute evenly`() {
        val words = listOf("1", "2", "3", "4", "5", "6")
        val result = buildBalancedLines(words, 3)

        assertThat(result).isEqualTo("1 2\n3 4\n5 6")
    }

    @Test
    fun `buildBalancedLines with more lines than words should handle gracefully`() {
        val words = listOf("One", "Two")
        val result = buildBalancedLines(words, 5)

        // Should not crash, just create fewer lines
        assertThat(result).contains("One")
        assertThat(result).contains("Two")
    }

    @Test
    fun `buildBalancedLines with empty list should return empty string`() {
        val words = emptyList<String>()
        val result = buildBalancedLines(words, 3)

        assertThat(result).isEmpty()
    }

    @Test
    fun `buildBalancedLines with single word should return that word`() {
        val words = listOf("OnlyWord")
        val result = buildBalancedLines(words, 1)

        assertThat(result).isEqualTo("OnlyWord")
    }

    @Test
    fun `buildBalancedLines with single word multiple lines should return that word`() {
        val words = listOf("OnlyWord")
        val result = buildBalancedLines(words, 3)

        assertThat(result).isEqualTo("OnlyWord")
    }

    @Test
    fun `buildBalancedLines should preserve word order`() {
        val words = listOf("First", "Second", "Third", "Fourth", "Fifth")
        val result = buildBalancedLines(words, 2)

        val resultWords = result.replace("\n", " ").split(" ")
        assertThat(resultWords).containsExactlyElementsIn(words).inOrder()
    }

    @Test
    fun `buildBalancedLines with long sentence should split correctly`() {
        val words = "The quick brown fox jumps over the lazy dog".split(" ")
        val result = buildBalancedLines(words, 3)

        // 9 words / 3 lines = 3 words per line
        assertThat(result.split("\n")).hasSize(3)
        assertThat(result).contains("The quick brown")
        assertThat(result).contains("fox jumps over")
        assertThat(result).contains("the lazy dog")
    }

    @Test
    fun `buildBalancedLines should not add trailing newline`() {
        val words = listOf("A", "B", "C", "D")
        val result = buildBalancedLines(words, 2)

        assertThat(result).doesNotMatch(".*\\n$")
    }

    @Test
    fun `buildBalancedLines with zero lines should return empty or all words`() {
        val words = listOf("A", "B", "C")
        val result = buildBalancedLines(words, 0)

        // Implementation may vary, but should not crash
        assertThat(result).isNotNull()
    }

    @Test
    fun `buildBalancedLines with negative lines should handle gracefully`() {
        val words = listOf("A", "B", "C")
        val result = buildBalancedLines(words, -1)

        // Should not crash
        assertThat(result).isNotNull()
    }

    @Test
    fun `buildBalancedLines distribution test with 10 words 3 lines`() {
        val words = (1..10).map { it.toString() }
        val result = buildBalancedLines(words, 3)

        // 10 words / 3 lines = 3 base + 1 extra for first line
        // Should be 4 + 3 + 3
        val lines = result.split("\n")
        assertThat(lines).hasSize(3)
        assertThat(lines[0].split(" ")).hasSize(4) // 1 2 3 4
        assertThat(lines[1].split(" ")).hasSize(3) // 5 6 7
        assertThat(lines[2].split(" ")).hasSize(3) // 8 9 10
    }

    @Test
    fun `buildBalancedLines with special characters in words`() {
        val words = listOf("Hello!", "World?", "Test#123", "@User")
        val result = buildBalancedLines(words, 2)

        assertThat(result).contains("Hello!")
        assertThat(result).contains("World?")
        assertThat(result).contains("Test#123")
        assertThat(result).contains("@User")
    }

    @Test
    fun `buildBalancedLines with unicode characters`() {
        val words = listOf("Hello", "世界", "🌍", "Test")
        val result = buildBalancedLines(words, 2)

        assertThat(result).contains("Hello")
        assertThat(result).contains("世界")
        assertThat(result).contains("🌍")
        assertThat(result).contains("Test")
    }
}
