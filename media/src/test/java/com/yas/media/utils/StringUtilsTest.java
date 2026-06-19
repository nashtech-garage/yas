package com.yas.media.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link StringUtils}.
 *
 * <p>Covers all branches of the single {@code hasText} method.
 */
@ExtendWith(MockitoExtension.class)
class StringUtilsTest {

    @Nested
    class HasTextTest {

        @Test
        void testHasText_whenInputIsNull_shouldReturnFalse() {
            assertThat(StringUtils.hasText(null)).isFalse();
        }

        @Test
        void testHasText_whenInputIsEmpty_shouldReturnFalse() {
            assertThat(StringUtils.hasText("")).isFalse();
        }

        @Test
        void testHasText_whenInputIsBlankSpaces_shouldReturnFalse() {
            assertThat(StringUtils.hasText("   ")).isFalse();
        }

        @Test
        void testHasText_whenInputIsNonBlankText_shouldReturnTrue() {
            assertThat(StringUtils.hasText("hello")).isTrue();
        }

        @Test
        void testHasText_whenInputHasLeadingAndTrailingSpaces_shouldReturnTrue() {
            assertThat(StringUtils.hasText("  hello  ")).isTrue();
        }

        @Test
        void testHasText_whenInputIsSingleCharacter_shouldReturnTrue() {
            assertThat(StringUtils.hasText("a")).isTrue();
        }
    }
}
