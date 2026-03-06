package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @Test
    void testHasTextWithNormalString() {
        assertTrue(StringUtils.hasText("Hello"));
        assertTrue(StringUtils.hasText("Test String"));
    }

    @Test
    void testHasTextWithNull() {
        assertFalse(StringUtils.hasText(null));
    }

    @Test
    void testHasTextWithEmptyString() {
        assertFalse(StringUtils.hasText(""));
    }

    @Test
    void testHasTextWithBlankString() {
        assertFalse(StringUtils.hasText("   "));
    }

    @Test
    void testHasTextWithTabAndNewline() {
        assertFalse(StringUtils.hasText("\t\n"));
    }

    @Test
    void testHasTextWithSingleSpace() {
        assertFalse(StringUtils.hasText(" "));
    }

    @Test
    void testHasTextWithMultipleSpaces() {
        assertFalse(StringUtils.hasText("     "));
    }

    @Test
    void testHasTextWithStringStartingWithSpace() {
        assertTrue(StringUtils.hasText("  Hello"));
    }

    @Test
    void testHasTextWithStringEndingWithSpace() {
        assertTrue(StringUtils.hasText("Hello  "));
    }

    @Test
    void testHasTextWithStringContainingSpaces() {
        assertTrue(StringUtils.hasText("Hello World"));
    }

    @Test
    void testHasTextWithSpecialCharacters() {
        assertTrue(StringUtils.hasText("@#$%^&*"));
    }

    @Test
    void testHasTextWithNumbers() {
        assertTrue(StringUtils.hasText("12345"));
    }

    @Test
    void testHasTextWithMixedContent() {
        assertTrue(StringUtils.hasText("Test123@#$" ));
    }

    @Test
    void testHasTextWithSingleCharacter() {
        assertTrue(StringUtils.hasText("A"));
    }

    @Test
    void testHasTextWithOnlyWhitespaceTypes() {
        assertFalse(StringUtils.hasText("   \t\n  "));
    }

    @Test
    void testHasTextWithLeadingAndTrailingWhitespace() {
        assertTrue(StringUtils.hasText("   Content   "));
    }
}
