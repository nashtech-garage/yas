package com.yas.media.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

/**
 * Unit tests for {@link FileTypeValidator}.
 *
 * <p>Covers: null file, null content-type, disallowed type, allowed type with
 * non-image bytes (ImageIO returns null), and allowed type with a broken stream (IOException).
 */
@ExtendWith(MockitoExtension.class)
class FileTypeValidatorTest {

    private FileTypeValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        ValidFileType annotation = mock(ValidFileType.class);
        when(annotation.allowedTypes()).thenReturn(new String[]{"image/jpeg", "image/png", "image/gif"});
        when(annotation.message()).thenReturn("File type not allowed");

        validator = new FileTypeValidator();
        validator.initialize(annotation);
    }

    /**
     * Stubs the context violation path.
     * Call only in tests where the code reaches the rejection branch
     * (null file, null content-type, or disallowed type).
     */
    private void stubContextViolation() {
        when(context.buildConstraintViolationWithTemplate("File type not allowed"))
            .thenReturn(violationBuilder);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // isValid
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    class IsValidTest {

        @Test
        void testIsValid_whenFileIsNull_shouldReturnFalse() {
            stubContextViolation();

            boolean result = validator.isValid(null, context);

            assertThat(result).isFalse();
        }

        @Test
        void testIsValid_whenContentTypeIsNull_shouldReturnFalse() throws IOException {
            stubContextViolation();
            MultipartFile file = mock(MultipartFile.class);
            when(file.getContentType()).thenReturn(null);

            boolean result = validator.isValid(file, context);

            assertThat(result).isFalse();
        }

        @Test
        void testIsValid_whenContentTypeNotAllowed_shouldReturnFalse() {
            stubContextViolation();
            MultipartFile file = mock(MultipartFile.class);
            when(file.getContentType()).thenReturn("application/pdf");

            boolean result = validator.isValid(file, context);

            assertThat(result).isFalse();
        }

        @Test
        // Content type matches, but bytes are not a real image → ImageIO.read() returns null
        // Code returns false BEFORE reaching the context violation path → no context stub needed
        void testIsValid_whenAllowedTypeButNotRealImage_shouldReturnFalse() throws IOException {
            MultipartFile file = mock(MultipartFile.class);
            when(file.getContentType()).thenReturn("image/png");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

            boolean result = validator.isValid(file, context);

            assertThat(result).isFalse();
        }

        @Test
        // Content type matches, but InputStream throws → IOException catch block returns false
        // Uses a real anonymous InputStream to avoid Mockito UnnecessaryStubbingException
        void testIsValid_whenGetInputStreamThrowsIOException_shouldReturnFalse() throws IOException {
            MultipartFile file = mock(MultipartFile.class);
            when(file.getContentType()).thenReturn("image/jpeg");
            when(file.getInputStream()).thenReturn(new InputStream() {
                @Override
                public int read() throws IOException {
                    throw new IOException("simulated broken stream");
                }
            });

            boolean result = validator.isValid(file, context);

            assertThat(result).isFalse();
        }
    }
}
