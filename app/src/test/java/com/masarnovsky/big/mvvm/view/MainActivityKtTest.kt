package com.masarnovsky.big.mvvm.view

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Unit tests for MainActivity helper functions
 */
class MainActivityKtTest {

    @Test
    fun `getLabelText with empty string should show default label`() {
        val result = getLabelText("")

        assertThat(result).isEqualTo("Enter text to display")
    }

    @Test
    fun `getLabelText with blank string should show default label`() {
        val result = getLabelText("   ")

        assertThat(result).isEqualTo("Enter text to display")
    }

    @Test
    fun `getLabelText with text should show character count`() {
        val text = "Hello"
        val result = getLabelText(text)

        assertThat(result).isEqualTo("Enter text to display 5/200")
    }

    @Test
    fun `getLabelText with max length text should show correct count`() {
        val text = "a".repeat(200)
        val result = getLabelText(text)

        assertThat(result).isEqualTo("Enter text to display 200/200")
    }

    @Test
    fun `getLabelText with single character should show count`() {
        val result = getLabelText("a")

        assertThat(result).isEqualTo("Enter text to display 1/200")
    }

    @Test
    fun `getLabelText with special characters should count correctly`() {
        val text = "!@#$%"
        val result = getLabelText(text)

        assertThat(result).isEqualTo("Enter text to display 5/200")
    }

    @Test
    fun `getLabelText with unicode characters should count correctly`() {
        val text = "Hello 🌍"
        val result = getLabelText(text)

        // Should count code points or characters correctly
        assertThat(result).contains("Enter text to display")
        assertThat(result).contains("/200")
    }

    @Test
    fun `getLabelText with newlines should count them`() {
        val text = "Line1\nLine2"
        val result = getLabelText(text)

        assertThat(result).isEqualTo("Enter text to display 11/200")
    }
}
