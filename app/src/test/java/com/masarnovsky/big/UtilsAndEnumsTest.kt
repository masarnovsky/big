package com.masarnovsky.big

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import com.google.common.truth.Truth.assertThat
import com.masarnovsky.big.mvvm.BackgroundColor
import com.masarnovsky.big.mvvm.GradientColor
import com.masarnovsky.big.mvvm.InputFont
import com.masarnovsky.big.mvvm.Orientation
import com.masarnovsky.big.mvvm.getBackgroundColor
import com.masarnovsky.big.mvvm.getRandomGradient
import com.masarnovsky.big.mvvm.getTextColor
import org.junit.Test

/**
 * Comprehensive tests for utility functions and enums
 *
 * Test Coverage:
 * - ✓ Background color generation (Black, White, Gradient)
 * - ✓ Text color selection based on background
 * - ✓ Random gradient generation
 * - ✓ Opacity handling (enabled/disabled state)
 * - ✓ All enum entries and values
 * - ✓ Enum valueOf() validation
 * - ✓ Font family validation
 * - ✓ Gradient color validation
 */
class UtilsAndEnumsTest {

    companion object {
        // Disabled alpha value (0.38f = 97/255 = 0.38039216f in actual float representation)
        private const val DISABLED_ALPHA = 0.38f
        private const val ALPHA_TOLERANCE = 0.01f
    }

    // ============================================================
    // BACKGROUND COLOR UTILITY TESTS
    // ============================================================

    @Test
    fun `getBackgroundColor should return black solid color when enabled`() {
        val brush = getBackgroundColor(BackgroundColor.BLACK, GradientColor.PURPLE_PINK, enabled = true)

        assertThat(brush).isInstanceOf(SolidColor::class.java)
        val solidColor = brush as SolidColor
        assertThat(solidColor.value).isEqualTo(Color.Black)
    }

    @Test
    fun `getBackgroundColor should return white solid color when enabled`() {
        val brush = getBackgroundColor(BackgroundColor.WHITE, GradientColor.PURPLE_PINK, enabled = true)

        assertThat(brush).isInstanceOf(SolidColor::class.java)
        val solidColor = brush as SolidColor
        assertThat(solidColor.value).isEqualTo(Color.White)
    }

    @Test
    fun `getBackgroundColor should return gradient brush when enabled`() {
        val brush = getBackgroundColor(BackgroundColor.GRADIENT, GradientColor.PURPLE_PINK, enabled = true)

        // Gradient returns a Brush (not SolidColor)
        assertThat(brush).isNotInstanceOf(SolidColor::class.java)
    }

    @Test
    fun `getBackgroundColor should return black with reduced opacity when disabled`() {
        val brush = getBackgroundColor(BackgroundColor.BLACK, GradientColor.PURPLE_PINK, enabled = false)

        assertThat(brush).isInstanceOf(SolidColor::class.java)
        val solidColor = brush as SolidColor
        assertThat(solidColor.value.alpha).isLessThan(1f)
        assertThat(solidColor.value.alpha).isWithin(ALPHA_TOLERANCE).of(DISABLED_ALPHA)
    }

    @Test
    fun `getBackgroundColor should return white with reduced opacity when disabled`() {
        val brush = getBackgroundColor(BackgroundColor.WHITE, GradientColor.PURPLE_PINK, enabled = false)

        assertThat(brush).isInstanceOf(SolidColor::class.java)
        val solidColor = brush as SolidColor
        assertThat(solidColor.value.alpha).isWithin(ALPHA_TOLERANCE).of(DISABLED_ALPHA)
    }

    @Test
    fun `getBackgroundColor should work with all gradient colors`() {
        GradientColor.entries.forEach { gradient ->
            val brush = getBackgroundColor(BackgroundColor.GRADIENT, gradient, enabled = true)
            assertThat(brush).isNotNull()
        }
    }

    // ============================================================
    // TEXT COLOR UTILITY TESTS
    // ============================================================

    @Test
    fun `getTextColor should return white for black background when enabled`() {
        val textColor = getTextColor(BackgroundColor.BLACK, enabled = true)

        assertThat(textColor).isEqualTo(Color.White)
    }

    @Test
    fun `getTextColor should return black for white background when enabled`() {
        val textColor = getTextColor(BackgroundColor.WHITE, enabled = true)

        assertThat(textColor).isEqualTo(Color.Black)
    }

    @Test
    fun `getTextColor should return white for gradient background when enabled`() {
        val textColor = getTextColor(BackgroundColor.GRADIENT, enabled = true)

        assertThat(textColor).isEqualTo(Color.White)
    }

    @Test
    fun `getTextColor should return white with reduced opacity for black background when disabled`() {
        val textColor = getTextColor(BackgroundColor.BLACK, enabled = false)

        assertThat(textColor.alpha).isWithin(ALPHA_TOLERANCE).of(DISABLED_ALPHA)
        assertThat(textColor.red).isEqualTo(Color.White.red)
        assertThat(textColor.green).isEqualTo(Color.White.green)
        assertThat(textColor.blue).isEqualTo(Color.White.blue)
    }

    @Test
    fun `getTextColor should return black with reduced opacity for white background when disabled`() {
        val textColor = getTextColor(BackgroundColor.WHITE, enabled = false)

        assertThat(textColor.alpha).isWithin(ALPHA_TOLERANCE).of(DISABLED_ALPHA)
        assertThat(textColor.red).isEqualTo(Color.Black.red)
    }

    @Test
    fun `getTextColor should return white with reduced opacity for gradient when disabled`() {
        val textColor = getTextColor(BackgroundColor.GRADIENT, enabled = false)

        assertThat(textColor.alpha).isWithin(ALPHA_TOLERANCE).of(DISABLED_ALPHA)
    }

    // ============================================================
    // RANDOM GRADIENT TESTS
    // ============================================================

    @Test
    fun `getRandomGradient should return valid gradient`() {
        val gradient = getRandomGradient()

        assertThat(GradientColor.entries).contains(gradient)
    }

    @Test
    fun `getRandomGradient should return gradients from available options`() {
        val gradients = mutableSetOf<GradientColor>()

        // Generate multiple gradients
        repeat(50) {
            gradients.add(getRandomGradient())
        }

        // All returned gradients should be valid
        gradients.forEach { gradient ->
            assertThat(GradientColor.entries).contains(gradient)
        }
    }

    @Test
    fun `getRandomGradient should potentially return different gradients`() {
        val gradients = (1..20).map { getRandomGradient() }

        // With 8 gradient options and 20 calls, we should see some variety
        // (This is probabilistic, but very unlikely to fail)
        val uniqueGradients = gradients.toSet()
        assertThat(uniqueGradients.size).isGreaterThan(1)
    }

    // ============================================================
    // BACKGROUND COLOR ENUM TESTS
    // ============================================================

    @Test
    fun `BackgroundColor should have exactly 3 entries`() {
        assertThat(BackgroundColor.entries).hasSize(3)
    }

    @Test
    fun `BackgroundColor should contain Black, White, and Gradient`() {
        val entries = BackgroundColor.entries
        assertThat(entries).contains(BackgroundColor.BLACK)
        assertThat(entries).contains(BackgroundColor.WHITE)
        assertThat(entries).contains(BackgroundColor.GRADIENT)
    }

    @Test
    fun `BackgroundColor labels should be correct`() {
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

    // ============================================================
    // ORIENTATION ENUM TESTS
    // ============================================================

    @Test
    fun `Orientation should have exactly 2 entries`() {
        assertThat(Orientation.entries).hasSize(2)
    }

    @Test
    fun `Orientation should contain Landscape and Portrait`() {
        val entries = Orientation.entries
        assertThat(entries).contains(Orientation.LANDSCAPE)
        assertThat(entries).contains(Orientation.PORTRAIT)
    }

    @Test
    fun `Orientation labels should be correct`() {
        assertThat(Orientation.LANDSCAPE.label).isEqualTo("Landscape")
        assertThat(Orientation.PORTRAIT.label).isEqualTo("Portrait")
    }

    @Test
    fun `Orientation valueOf should work for all entries`() {
        assertThat(Orientation.valueOf("LANDSCAPE")).isEqualTo(Orientation.LANDSCAPE)
        assertThat(Orientation.valueOf("PORTRAIT")).isEqualTo(Orientation.PORTRAIT)
    }

    // ============================================================
    // INPUT FONT ENUM TESTS
    // ============================================================

    @Test
    fun `InputFont should have exactly 4 entries`() {
        assertThat(InputFont.entries).hasSize(4)
    }

    @Test
    fun `InputFont should contain all font options`() {
        val entries = InputFont.entries
        assertThat(entries).contains(InputFont.MONTSERRAT)
        assertThat(entries).contains(InputFont.PANGOLIN)
        assertThat(entries).contains(InputFont.ROBOTO_SLAB)
        assertThat(entries).contains(InputFont.PLAYFAIR_DISPLAY)
    }

    @Test
    fun `InputFont labels should be correct`() {
        assertThat(InputFont.MONTSERRAT.label).isEqualTo("Montserrat")
        assertThat(InputFont.PANGOLIN.label).isEqualTo("Pangolin")
        assertThat(InputFont.ROBOTO_SLAB.label).isEqualTo("Roboto")
        assertThat(InputFont.PLAYFAIR_DISPLAY.label).isEqualTo("Playfair")
    }

    @Test
    fun `InputFont valueOf should work for all entries`() {
        assertThat(InputFont.valueOf("MONTSERRAT")).isEqualTo(InputFont.MONTSERRAT)
        assertThat(InputFont.valueOf("PANGOLIN")).isEqualTo(InputFont.PANGOLIN)
        assertThat(InputFont.valueOf("ROBOTO_SLAB")).isEqualTo(InputFont.ROBOTO_SLAB)
        assertThat(InputFont.valueOf("PLAYFAIR_DISPLAY")).isEqualTo(InputFont.PLAYFAIR_DISPLAY)
    }

    @Test
    fun `InputFont should have non-null fontFamily for all entries`() {
        InputFont.entries.forEach { font ->
            assertThat(font.fontFamily).isNotNull()
        }
    }

    // ============================================================
    // GRADIENT COLOR ENUM TESTS
    // ============================================================

    @Test
    fun `GradientColor should have exactly 8 entries`() {
        assertThat(GradientColor.entries).hasSize(8)
    }

    @Test
    fun `GradientColor should contain all gradient options`() {
        val entries = GradientColor.entries
        assertThat(entries).contains(GradientColor.PURPLE_PINK)
        assertThat(entries).contains(GradientColor.BLUE_PURPLE)
        assertThat(entries).contains(GradientColor.PINK_ORANGE)
        assertThat(entries).contains(GradientColor.TEAL_BLUE)
        assertThat(entries).contains(GradientColor.RED_PURPLE)
        assertThat(entries).contains(GradientColor.DARK_PURPLE_CYAN)
        assertThat(entries).contains(GradientColor.YELLOW_GREEN)
        assertThat(entries).contains(GradientColor.PEACH_RED)
    }

    @Test
    fun `GradientColor display names should be set`() {
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

    @Test
    fun `GradientColor should have exactly 2 colors in each gradient`() {
        GradientColor.entries.forEach { gradient ->
            assertThat(gradient.colors).hasSize(2)
        }
    }

    @Test
    fun `GradientColor colors should not be null or empty`() {
        GradientColor.entries.forEach { gradient ->
            assertThat(gradient.colors).isNotEmpty()
            gradient.colors.forEach { color ->
                assertThat(color).isNotNull()
            }
        }
    }

    @Test
    fun `GradientColor toBrush should return valid brush with full alpha`() {
        val gradient = GradientColor.PURPLE_PINK
        val brush = gradient.toBrush(1f)

        assertThat(brush).isNotNull()
        assertThat(brush).isNotInstanceOf(SolidColor::class.java)
    }

    @Test
    fun `GradientColor toBrush should work with reduced alpha`() {
        val gradient = GradientColor.BLUE_PURPLE
        val brush = gradient.toBrush(0.5f)

        assertThat(brush).isNotNull()
    }

    @Test
    fun `GradientColor toBrush should work for all gradients`() {
        GradientColor.entries.forEach { gradient ->
            val brush = gradient.toBrush(1f)
            assertThat(brush).isNotNull()
        }
    }

    // ============================================================
    // GRADIENT COLOR VALUES TESTS
    // ============================================================

    @Test
    fun `PURPLE_PINK gradient should have correct colors`() {
        val gradient = GradientColor.PURPLE_PINK
        assertThat(gradient.colors[0]).isEqualTo(Color(0xFF6200EE))
        assertThat(gradient.colors[1]).isEqualTo(Color(0xFFBB86FC))
    }

    @Test
    fun `BLUE_PURPLE gradient should have correct colors`() {
        val gradient = GradientColor.BLUE_PURPLE
        assertThat(gradient.colors[0]).isEqualTo(Color(0xFF2196F3))
        assertThat(gradient.colors[1]).isEqualTo(Color(0xFF9C27B0))
    }

    @Test
    fun `PINK_ORANGE gradient should have correct colors`() {
        val gradient = GradientColor.PINK_ORANGE
        assertThat(gradient.colors[0]).isEqualTo(Color(0xFFE91E63))
        assertThat(gradient.colors[1]).isEqualTo(Color(0xFFFF9800))
    }

    @Test
    fun `TEAL_BLUE gradient should have correct colors`() {
        val gradient = GradientColor.TEAL_BLUE
        assertThat(gradient.colors[0]).isEqualTo(Color(0xFF009688))
        assertThat(gradient.colors[1]).isEqualTo(Color(0xFF2196F3))
    }

    @Test
    fun `RED_PURPLE gradient should have correct colors`() {
        val gradient = GradientColor.RED_PURPLE
        assertThat(gradient.colors[0]).isEqualTo(Color(0xFFF44336))
        assertThat(gradient.colors[1]).isEqualTo(Color(0xFF9C27B0))
    }

    @Test
    fun `DARK_PURPLE_CYAN gradient should have correct colors`() {
        val gradient = GradientColor.DARK_PURPLE_CYAN
        assertThat(gradient.colors[0]).isEqualTo(Color(0xFF42047e))
        assertThat(gradient.colors[1]).isEqualTo(Color(0xFF07f49e))
    }

    @Test
    fun `YELLOW_GREEN gradient should have correct colors`() {
        val gradient = GradientColor.YELLOW_GREEN
        assertThat(gradient.colors[0]).isEqualTo(Color(0xFFf4f269))
        assertThat(gradient.colors[1]).isEqualTo(Color(0xFF5cb270))
    }

    @Test
    fun `PEACH_RED gradient should have correct colors`() {
        val gradient = GradientColor.PEACH_RED
        assertThat(gradient.colors[0]).isEqualTo(Color(0xFFffb88e))
        assertThat(gradient.colors[1]).isEqualTo(Color(0xFFea5753))
    }

    // ============================================================
    // INTEGRATION TESTS
    // ============================================================

    @Test
    fun `all enum combinations should be valid`() {
        // Test that all combinations of enums work together
        InputFont.entries.forEach { font ->
            BackgroundColor.entries.forEach { background ->
                Orientation.entries.forEach { orientation ->
                    GradientColor.entries.forEach { gradient ->
                        // All combinations should be valid
                        assertThat(font).isNotNull()
                        assertThat(background).isNotNull()
                        assertThat(orientation).isNotNull()
                        assertThat(gradient).isNotNull()
                    }
                }
            }
        }
    }

    @Test
    fun `getBackgroundColor should work with all enum combinations`() {
        BackgroundColor.entries.forEach { background ->
            GradientColor.entries.forEach { gradient ->
                val brushEnabled = getBackgroundColor(background, gradient, enabled = true)
                val brushDisabled = getBackgroundColor(background, gradient, enabled = false)

                assertThat(brushEnabled).isNotNull()
                assertThat(brushDisabled).isNotNull()
            }
        }
    }

    @Test
    fun `getTextColor should work with all background colors`() {
        BackgroundColor.entries.forEach { background ->
            val textColorEnabled = getTextColor(background, enabled = true)
            val textColorDisabled = getTextColor(background, enabled = false)

            assertThat(textColorEnabled).isNotNull()
            assertThat(textColorDisabled).isNotNull()
        }
    }
}
