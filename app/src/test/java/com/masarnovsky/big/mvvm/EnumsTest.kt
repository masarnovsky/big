package com.masarnovsky.big.mvvm

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Unit tests for Enum classes in Enums.kt
 */
class EnumsTest {

    // BackgroundColor Tests

    @Test
    fun `BackgroundColor should have correct number of entries`() {
        assertThat(BackgroundColor.entries).hasSize(3)
    }

    @Test
    fun `BackgroundColor entries should have correct labels`() {
        assertThat(BackgroundColor.BLACK.label).isEqualTo("Black")
        assertThat(BackgroundColor.WHITE.label).isEqualTo("White")
        assertThat(BackgroundColor.GRADIENT.label).isEqualTo("Gradient")
    }

    @Test
    fun `BackgroundColor valueOf should work for all entries`() {
        assertThat(BackgroundColor.valueOf("BLACK")).isEqualTo(BackgroundColor.BLACK)
        assertThat(BackgroundColor.valueOf("WHITE")).isEqualTo(BackgroundColor.WHITE)
        assertThat(BackgroundColor.valueOf("GRADIENT")).isEqualTo(BackgroundColor.GRADIENT)
    }

    // Orientation Tests

    @Test
    fun `Orientation should have correct number of entries`() {
        assertThat(Orientation.entries).hasSize(2)
    }

    @Test
    fun `Orientation entries should have correct labels`() {
        assertThat(Orientation.LANDSCAPE.label).isEqualTo("Landscape")
        assertThat(Orientation.PORTRAIT.label).isEqualTo("Portrait")
    }

    @Test
    fun `Orientation valueOf should work for all entries`() {
        assertThat(Orientation.valueOf("LANDSCAPE")).isEqualTo(Orientation.LANDSCAPE)
        assertThat(Orientation.valueOf("PORTRAIT")).isEqualTo(Orientation.PORTRAIT)
    }

    // InputFont Tests

    @Test
    fun `InputFont should have correct number of entries`() {
        assertThat(InputFont.entries).hasSize(4)
    }

    @Test
    fun `InputFont entries should have correct labels`() {
        assertThat(InputFont.MONTSERRAT.label).isEqualTo("Montserrat")
        assertThat(InputFont.PANGOLIN.label).isEqualTo("Pangolin")
        assertThat(InputFont.ROBOTO_SLAB.label).isEqualTo("Roboto")
        assertThat(InputFont.PLAYFAIR_DISPLAY.label).isEqualTo("Playfair")
    }

    @Test
    fun `InputFont entries should have non-null font families`() {
        for (font in InputFont.entries) {
            assertThat(font.fontFamily).isNotNull()
        }
    }

    @Test
    fun `InputFont valueOf should work for all entries`() {
        assertThat(InputFont.valueOf("MONTSERRAT")).isEqualTo(InputFont.MONTSERRAT)
        assertThat(InputFont.valueOf("PANGOLIN")).isEqualTo(InputFont.PANGOLIN)
        assertThat(InputFont.valueOf("ROBOTO_SLAB")).isEqualTo(InputFont.ROBOTO_SLAB)
        assertThat(InputFont.valueOf("PLAYFAIR_DISPLAY")).isEqualTo(InputFont.PLAYFAIR_DISPLAY)
    }

    // GradientColor Tests

    @Test
    fun `GradientColor should have correct number of entries`() {
        assertThat(GradientColor.entries).hasSize(8)
    }

    @Test
    fun `GradientColor entries should have correct display names`() {
        assertThat(GradientColor.PURPLE_PINK.displayName).isEqualTo("Purple to Pink")
        assertThat(GradientColor.BLUE_PURPLE.displayName).isEqualTo("Blue to Purple")
        assertThat(GradientColor.PINK_ORANGE.displayName).isEqualTo("Pink to Orange")
        assertThat(GradientColor.TEAL_BLUE.displayName).isEqualTo("Teal to Blue")
        assertThat(GradientColor.RED_PURPLE.displayName).isEqualTo("Red to Purple")
        assertThat(GradientColor.DARK_PURPLE_CYAN.displayName).isEqualTo("Dark Purple to Cyan")
        assertThat(GradientColor.YELLOW_GREEN.displayName).isEqualTo("Yellow to Green")
        assertThat(GradientColor.PEACH_RED.displayName).isEqualTo("Peach to Red")
    }

    @Test
    fun `GradientColor entries should have exactly 2 colors`() {
        for (gradient in GradientColor.entries) {
            assertThat(gradient.colors).hasSize(2)
        }
    }

    @Test
    fun `GradientColor PURPLE_PINK should have correct colors`() {
        val colors = GradientColor.PURPLE_PINK.colors
        assertThat(colors[0]).isEqualTo(Color(0xFF6200EE))
        assertThat(colors[1]).isEqualTo(Color(0xFFBB86FC))
    }

    @Test
    fun `GradientColor BLUE_PURPLE should have correct colors`() {
        val colors = GradientColor.BLUE_PURPLE.colors
        assertThat(colors[0]).isEqualTo(Color(0xFF2196F3))
        assertThat(colors[1]).isEqualTo(Color(0xFF9C27B0))
    }

    @Test
    fun `GradientColor PINK_ORANGE should have correct colors`() {
        val colors = GradientColor.PINK_ORANGE.colors
        assertThat(colors[0]).isEqualTo(Color(0xFFE91E63))
        assertThat(colors[1]).isEqualTo(Color(0xFFFF9800))
    }

    @Test
    fun `GradientColor TEAL_BLUE should have correct colors`() {
        val colors = GradientColor.TEAL_BLUE.colors
        assertThat(colors[0]).isEqualTo(Color(0xFF009688))
        assertThat(colors[1]).isEqualTo(Color(0xFF2196F3))
    }

    @Test
    fun `GradientColor toBrush should return non-null brush`() {
        for (gradient in GradientColor.entries) {
            val brush = gradient.toBrush(1f)
            assertThat(brush).isNotNull()
        }
    }

    @Test
    fun `GradientColor toBrush should handle different alpha values`() {
        val gradient = GradientColor.PURPLE_PINK

        val fullAlpha = gradient.toBrush(1f)
        val halfAlpha = gradient.toBrush(0.5f)
        val zeroAlpha = gradient.toBrush(0f)

        assertThat(fullAlpha).isNotNull()
        assertThat(halfAlpha).isNotNull()
        assertThat(zeroAlpha).isNotNull()
    }

    @Test
    fun `GradientColor valueOf should work for all entries`() {
        assertThat(GradientColor.valueOf("PURPLE_PINK")).isEqualTo(GradientColor.PURPLE_PINK)
        assertThat(GradientColor.valueOf("BLUE_PURPLE")).isEqualTo(GradientColor.BLUE_PURPLE)
        assertThat(GradientColor.valueOf("PINK_ORANGE")).isEqualTo(GradientColor.PINK_ORANGE)
        assertThat(GradientColor.valueOf("TEAL_BLUE")).isEqualTo(GradientColor.TEAL_BLUE)
        assertThat(GradientColor.valueOf("RED_PURPLE")).isEqualTo(GradientColor.RED_PURPLE)
        assertThat(GradientColor.valueOf("DARK_PURPLE_CYAN")).isEqualTo(GradientColor.DARK_PURPLE_CYAN)
        assertThat(GradientColor.valueOf("YELLOW_GREEN")).isEqualTo(GradientColor.YELLOW_GREEN)
        assertThat(GradientColor.valueOf("PEACH_RED")).isEqualTo(GradientColor.PEACH_RED)
    }

    // Cross-enum Tests

    @Test
    fun `all enums should have unique values`() {
        assertThat(BackgroundColor.entries.map { it.name }).containsNoDuplicates()
        assertThat(Orientation.entries.map { it.name }).containsNoDuplicates()
        assertThat(InputFont.entries.map { it.name }).containsNoDuplicates()
        assertThat(GradientColor.entries.map { it.name }).containsNoDuplicates()
    }

    @Test
    fun `all enums should have non-empty labels or display names`() {
        for (bg in BackgroundColor.entries) {
            assertThat(bg.label).isNotEmpty()
        }
        for (orientation in Orientation.entries) {
            assertThat(orientation.label).isNotEmpty()
        }
        for (font in InputFont.entries) {
            assertThat(font.label).isNotEmpty()
        }
        for (gradient in GradientColor.entries) {
            assertThat(gradient.displayName).isNotEmpty()
        }
    }
}
