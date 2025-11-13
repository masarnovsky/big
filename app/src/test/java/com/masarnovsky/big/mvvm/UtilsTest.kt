package com.masarnovsky.big.mvvm

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Unit tests for utility functions in Utils.kt
 */
class UtilsTest {

    // getBackgroundColor Tests

    @Test
    fun `getBackgroundColor with BLACK should return black solid color`() {
        val result = getBackgroundColor(
            background = BackgroundColor.BLACK,
            gradient = GradientColor.PURPLE_PINK,
            enabled = true
        )

        assertThat(result).isInstanceOf(SolidColor::class.java)
        val solidColor = result as SolidColor
        assertThat(solidColor.value).isEqualTo(Color.Black)
    }

    @Test
    fun `getBackgroundColor with WHITE should return white solid color`() {
        val result = getBackgroundColor(
            background = BackgroundColor.WHITE,
            gradient = GradientColor.PURPLE_PINK,
            enabled = true
        )

        assertThat(result).isInstanceOf(SolidColor::class.java)
        val solidColor = result as SolidColor
        assertThat(solidColor.value).isEqualTo(Color.White)
    }

    @Test
    fun `getBackgroundColor with GRADIENT should return gradient brush`() {
        val result = getBackgroundColor(
            background = BackgroundColor.GRADIENT,
            gradient = GradientColor.BLUE_PURPLE,
            enabled = true
        )

        // LinearGradient is a type of Brush but not SolidColor
        assertThat(result).isNotInstanceOf(SolidColor::class.java)
        assertThat(result).isInstanceOf(Brush::class.java)
    }

    @Test
    fun `getBackgroundColor with enabled=false should have reduced opacity`() {
        val enabledResult = getBackgroundColor(
            background = BackgroundColor.BLACK,
            gradient = GradientColor.PURPLE_PINK,
            enabled = true
        ) as SolidColor

        val disabledResult = getBackgroundColor(
            background = BackgroundColor.BLACK,
            gradient = GradientColor.PURPLE_PINK,
            enabled = false
        ) as SolidColor

        // Enabled should have alpha = 1.0
        assertThat(enabledResult.value.alpha).isEqualTo(1f)
        // Disabled should have alpha = 0.38
        assertThat(disabledResult.value.alpha).isEqualTo(0.38f)
    }

    @Test
    fun `getBackgroundColor with all gradient types should work`() {
        for (gradient in GradientColor.entries) {
            val result = getBackgroundColor(
                background = BackgroundColor.GRADIENT,
                gradient = gradient,
                enabled = true
            )

            assertThat(result).isNotNull()
            assertThat(result).isInstanceOf(Brush::class.java)
        }
    }

    // getTextColor Tests

    @Test
    fun `getTextColor with WHITE background should return black`() {
        val result = getTextColor(
            background = BackgroundColor.WHITE,
            enabled = true
        )

        assertThat(result).isEqualTo(Color.Black)
    }

    @Test
    fun `getTextColor with BLACK background should return white`() {
        val result = getTextColor(
            background = BackgroundColor.BLACK,
            enabled = true
        )

        assertThat(result).isEqualTo(Color.White)
    }

    @Test
    fun `getTextColor with GRADIENT background should return white`() {
        val result = getTextColor(
            background = BackgroundColor.GRADIENT,
            enabled = true
        )

        assertThat(result).isEqualTo(Color.White)
    }

    @Test
    fun `getTextColor with enabled=false should have reduced opacity`() {
        val enabledResult = getTextColor(
            background = BackgroundColor.BLACK,
            enabled = true
        )

        val disabledResult = getTextColor(
            background = BackgroundColor.BLACK,
            enabled = false
        )

        assertThat(enabledResult.alpha).isEqualTo(1f)
        assertThat(disabledResult.alpha).isEqualTo(0.38f)
    }

    @Test
    fun `getTextColor with all backgrounds should return valid colors`() {
        for (background in BackgroundColor.entries) {
            val result = getTextColor(background, enabled = true)

            assertThat(result).isNotNull()
            assertThat(result.alpha).isEqualTo(1f)
        }
    }

    // getRandomGradient Tests

    @Test
    fun `getRandomGradient should return valid gradient`() {
        val result = getRandomGradient()

        assertThat(result).isNotNull()
        assertThat(result).isIn(GradientColor.entries)
    }

    @Test
    fun `getRandomGradient should return different values over multiple calls`() {
        val results = mutableSetOf<GradientColor>()

        // Call 100 times to get variety
        repeat(100) {
            results.add(getRandomGradient())
        }

        // Should have at least 2 different gradients (very likely with 100 calls)
        assertThat(results.size).isAtLeast(2)
    }

    @Test
    fun `getRandomGradient should only return valid enum values`() {
        repeat(50) {
            val result = getRandomGradient()
            assertThat(GradientColor.entries).contains(result)
        }
    }

    // Integration Tests

    @Test
    fun `background and text color should have good contrast`() {
        // Black background should have white text
        val blackBg = getBackgroundColor(BackgroundColor.BLACK, GradientColor.PURPLE_PINK)
        val textOnBlack = getTextColor(BackgroundColor.BLACK)
        assertThat(textOnBlack).isEqualTo(Color.White)

        // White background should have black text
        val whiteBg = getBackgroundColor(BackgroundColor.WHITE, GradientColor.PURPLE_PINK)
        val textOnWhite = getTextColor(BackgroundColor.WHITE)
        assertThat(textOnWhite).isEqualTo(Color.Black)
    }

    @Test
    fun `disabled state should affect both background and text`() {
        val bgEnabled = getBackgroundColor(BackgroundColor.BLACK, GradientColor.PURPLE_PINK, true) as SolidColor
        val bgDisabled = getBackgroundColor(BackgroundColor.BLACK, GradientColor.PURPLE_PINK, false) as SolidColor

        val textEnabled = getTextColor(BackgroundColor.BLACK, true)
        val textDisabled = getTextColor(BackgroundColor.BLACK, false)

        assertThat(bgEnabled.value.alpha).isGreaterThan(bgDisabled.value.alpha)
        assertThat(textEnabled.alpha).isGreaterThan(textDisabled.alpha)
    }
}
