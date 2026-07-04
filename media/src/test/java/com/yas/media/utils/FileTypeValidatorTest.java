package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class FileTypeValidatorTest {

    private FileTypeValidator validator;
    private ConstraintValidatorContext context;
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        validator = new FileTypeValidator();
        context = mock(ConstraintValidatorContext.class);
        violationBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);

        // Initialize validator with allowed types
        ValidFileType annotation = mock(ValidFileType.class);
        when(annotation.allowedTypes()).thenReturn(new String[]{"image/jpeg", "image/png", "image/gif"});
        when(annotation.message()).thenReturn("File type not allowed");
        validator.initialize(annotation);
    }

    @Test
    void isValid_whenFileIsNull_thenReturnFalse() {
        boolean result = validator.isValid(null, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("File type not allowed");
    }

    @Test
    void isValid_whenContentTypeIsNull_thenReturnFalse() {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", null, new byte[]{});

        boolean result = validator.isValid(file, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void isValid_whenContentTypeNotAllowed_thenReturnFalse() {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.pdf", "application/pdf", "pdf-content".getBytes()
        );

        boolean result = validator.isValid(file, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void isValid_whenValidPngImage_thenReturnTrue() throws IOException {
        byte[] imageBytes = createValidImageBytes("png");
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.png", "image/png", imageBytes
        );

        boolean result = validator.isValid(file, context);

        assertTrue(result);
        verify(context, never()).disableDefaultConstraintViolation();
    }

    @Test
    void isValid_whenValidJpegImage_thenReturnTrue() throws IOException {
        byte[] imageBytes = createValidImageBytes("jpg");
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", imageBytes
        );

        boolean result = validator.isValid(file, context);

        assertTrue(result);
        verify(context, never()).disableDefaultConstraintViolation();
    }

    @Test
    void isValid_whenAllowedTypeButInvalidImageData_thenReturnFalse() {
        // Content type matches but the actual data is not a valid image
        MockMultipartFile file = new MockMultipartFile(
            "file", "fake.png", "image/png", "this-is-not-an-image".getBytes()
        );

        boolean result = validator.isValid(file, context);

        assertFalse(result);
    }

    /**
     * Creates a minimal valid image byte array for testing.
     */
    private byte[] createValidImageBytes(String format) throws IOException {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return baos.toByteArray();
    }
}
