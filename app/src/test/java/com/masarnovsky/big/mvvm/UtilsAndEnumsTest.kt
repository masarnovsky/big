package com.masarnovsky.big.mvvm

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import com.google.common.truth.Truth.assertThat
import com.masarnovsky.big.mvvm.view.components.buildBalancedLines
import org.junit.Test

/**
 * Comprehensive tests for utility functions and enums.
 * Covers green (happy path), red (error), and tricky (edge) cases.
 */
class UtilsAndEnumsTest {

    // ===== Utils Tests - Background Color =====

    @Test
    fun `getBackgroundColor BLACK returns black solid color`() {
        val result = getBackgroundColor(BackgroundColor.BLACK, GradientColor.PURPLE_PINK, true)

        assertThat(result).isInstanceOf(SolidColor::class.java)
        val solidColor = result as SolidColor
        assertThat(solidColor.value).isEqualTo(Color.Black)
    }

    @Test
    fun `getBackgroundColor WHITE returns white solid color`() {
        val result = getBackgroundColor(BackgroundColor.WHITE, GradientColor.PURPLE_PINK, true)

        assertThat(result).isInstanceOf(SolidColor::class.java)
        val solidColor = result as SolidColor
        assertThat(solidColor.value).isEqualTo(Color.White)
    }

    @Test
    fun `getBackgroundColor GRADIENT returns gradient brush`() {
        val result = getBackgroundColor(BackgroundColor.GRADIENT, GradientColor.BLUE_PURPLE, true)

        assertThat(result).isNotInstanceOf(SolidColor::class.java)
        assertThat(result).isInstanceOf(Brush::class.java)
    }

    @Test
    fun `getBackgroundColor disabled has reduced opacity`() {
        val enabled = getBackgroundColor(BackgroundColor.BLACK, GradientColor.PURPLE_PINK, true) as SolidColor
        val disabled = getBackgroundColor(BackgroundColor.BLACK, GradientColor.PURPLE_PINK, false) as SolidColor

        assertThat(enabled.value.alpha).isEqualTo(1f)
        assertThat(disabled.value.alpha).isEqualTo(0.38f)
    }

    @Test
    fun `getTextColor WHITE background returns black`() {
        val result = getTextColor(BackgroundColor.WHITE, true)
        assertThat(result).isEqualTo(Color.Black)
    }

    @Test
    fun `getTextColor BLACK background returns white`() {
        val result = getTextColor(BackgroundColor.BLACK, true)
        assertThat(result).isEqualTo(Color.White)
    }

    @Test
    fun `getTextColor GRADIENT background returns white`() {
        val result = getTextColor(BackgroundColor.GRADIENT, true)
        assertThat(result).isEqualTo(Color.White)
    }

    @Test
    fun `getRandomGradient returns valid gradient`() {
        repeat(50) {
            val result = getRandomGradient()
            assertThat(GradientColor.entries).contains(result)
        }
    }

    @Test
    fun `getRandomGradient produces variety`() {
        val results = mutableSetOf<GradientColor>()
        repeat(100) {
            results.add(getRandomGradient())
        }
        assertThat(results.size).isAtLeast(2)
    }

    // ===== Enum Tests =====

    @Test
    fun `BackgroundColor has correct entries`() {
        assertThat(BackgroundColor.entries).hasSize(3)
        assertThat(BackgroundColor.entries).containsExactly(
            BackgroundColor.BLACK,
            BackgroundColor.WHITE,
            BackgroundColor.GRADIENT
        )
    }

    @Test
    fun `Orientation has correct entries`() {
        assertThat(Orientation.entries).hasSize(2)
        assertThat(Orientation.entries).containsExactly(
            Orientation.LANDSCAPE,
            Orientation.PORTRAIT
        )
    }

    @Test
    fun `InputFont has correct entries`() {
        assertThat(InputFont.entries).hasSize(4)
        InputFont.entries.forEach { font ->
            assertThat(font.fontFamily).isNotNull()
        }
    }

    @Test
    fun `GradientColor has correct entries`() {
        assertThat(GradientColor.entries).hasSize(8)
        GradientColor.entries.forEach { gradient ->
            assertThat(gradient.colors).hasSize(2)
        }
    }

    @Test
    fun `GradientColor toBrush returns non-null`() {
        GradientColor.entries.forEach { gradient ->
            val brush = gradient.toBrush(1f)
            assertThat(brush).isNotNull()
        }
    }

    @Test
    fun `enum valueOf works for all values`() {
        // BackgroundColor
        assertThat(BackgroundColor.valueOf("BLACK")).isEqualTo(BackgroundColor.BLACK)
        assertThat(BackgroundColor.valueOf("WHITE")).isEqualTo(BackgroundColor.WHITE)
        assertThat(BackgroundColor.valueOf("GRADIENT")).isEqualTo(BackgroundColor.GRADIENT)

        // Orientation
        assertThat(Orientation.valueOf("LANDSCAPE")).isEqualTo(Orientation.LANDSCAPE)
        assertThat(Orientation.valueOf("PORTRAIT")).isEqualTo(Orientation.PORTRAIT)

        // InputFont
        assertThat(InputFont.valueOf("MONTSERRAT")).isEqualTo(InputFont.MONTSERRAT)
        assertThat(InputFont.valueOf("PANGOLIN")).isEqualTo(InputFont.PANGOLIN)
    }

    // ===== AutoSize Text Algorithm Tests =====

    @Test
    fun `buildBalancedLines single line joins all words`() {
        val words = listOf("Hello", "World", "Test")
        val result = buildBalancedLines(words, 1)
        assertThat(result).isEqualTo("Hello World Test")
    }

    @Test
    fun `buildBalancedLines two lines splits evenly`() {
        val words = listOf("One", "Two", "Three", "Four")
        val result = buildBalancedLines(words, 2)
        assertThat(result).isEqualTo("One Two\nThree Four")
    }

    @Test
    fun `buildBalancedLines preserves word order`() {
        val words = listOf("First", "Second", "Third", "Fourth", "Fifth")
        val result = buildBalancedLines(words, 2)
        val resultWords = result.replace("\n", " ").split(" ")
        assertThat(resultWords).containsExactlyElementsIn(words).inOrder()
    }

    @Test
    fun `buildBalancedLines empty list returns empty string`() {
        val result = buildBalancedLines(emptyList(), 3)
        assertThat(result).isEmpty()
    }

    @Test
    fun `buildBalancedLines single word returns that word`() {
        val result = buildBalancedLines(listOf("OnlyWord"), 1)
        assertThat(result).isEqualTo("OnlyWord")
    }

    @Test
    fun `buildBalancedLines handles emoji words`() {
        val words = listOf("Hello", "🌍", "World", "🎉")
        val result = buildBalancedLines(words, 2)
        assertThat(result).contains("Hello")
        assertThat(result).contains("🌍")
        assertThat(result).contains("World")
        assertThat(result).contains("🎉")
    }

    @Test
    fun `buildBalancedLines handles special characters`() {
        val words = listOf("Hello!", "World?", "Test#123", "@User")
        val result = buildBalancedLines(words, 2)
        assertThat(result).contains("Hello!")
        assertThat(result).contains("World?")
    }

    @Test
    fun `buildBalancedLines no trailing newline`() {
        val words = listOf("A", "B", "C", "D")
        val result = buildBalancedLines(words, 2)
        assertThat(result).doesNotMatch(".*\\n\$")
    }

    @Test
    fun `buildBalancedLines with 10 words 3 lines distributes correctly`() {
        val words = (1..10).map { it.toString() }
        val result = buildBalancedLines(words, 3)
        val lines = result.split("\n")
        assertThat(lines).hasSize(3)
        assertThat(lines[0].split(" ")).hasSize(4) // 4 words
        assertThat(lines[1].split(" ")).hasSize(3) // 3 words
        assertThat(lines[2].split(" ")).hasSize(3) // 3 words
    }

    // ===== Edge Cases =====

    @Test
    fun `all gradient colors have unique display names`() {
        val displayNames = GradientColor.entries.map { it.displayName }
        assertThat(displayNames).containsNoDuplicates()
    }

    @Test
    fun `all enums have unique names`() {
        assertThat(BackgroundColor.entries.map { it.name }).containsNoDuplicates()
        assertThat(Orientation.entries.map { it.name }).containsNoDuplicates()
        assertThat(InputFont.entries.map { it.name }).containsNoDuplicates()
        assertThat(GradientColor.entries.map { it.name }).containsNoDuplicates()
    }

    @Test
    fun `all font labels are non-empty`() {
        InputFont.entries.forEach { font ->
            assertThat(font.label).isNotEmpty()
        }
    }

    @Test
    fun `gradient colors are valid`() {
        GradientColor.entries.forEach { gradient ->
            gradient.colors.forEach { color ->
                assertThat(color.alpha).isIn(0f..1f)
            }
        }
    }
}
