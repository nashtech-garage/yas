package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class FileTypeValidatorTest {

    private FileTypeValidator validator;
    private ConstraintValidatorContext context;
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        validator = new FileTypeValidator();

        // Create a mock annotation
        ValidFileType annotation = mock(ValidFileType.class);
        when(annotation.allowedTypes()).thenReturn(new String[]{"image/jpeg", "image/png", "image/gif"});
        when(annotation.message()).thenReturn("File type not allowed");

        validator.initialize(annotation);

        context = mock(ConstraintValidatorContext.class);
        violationBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(org.mockito.ArgumentMatchers.anyString()))
            .thenReturn(violationBuilder);
    }

    @Test
    void isValid_whenFileIsNull_thenReturnFalse() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    void isValid_whenContentTypeIsNull_thenReturnFalse() {
        MultipartFile file = new MockMultipartFile("file", "test.txt", null, "data".getBytes());
        assertFalse(validator.isValid(file, context));
    }

    @Test
    void isValid_whenTypeNotAllowed_thenReturnFalse() {
        MultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "data".getBytes());
        assertFalse(validator.isValid(file, context));
    }

    @Test
    void isValid_whenValidPngImage_thenReturnTrue() throws IOException {
        byte[] imageBytes = createValidPngBytes();
        MultipartFile file = new MockMultipartFile("file", "image.png", "image/png", imageBytes);
        assertTrue(validator.isValid(file, context));
    }

    @Test
    void isValid_whenAllowedTypeButNotRealImage_thenReturnFalse() {
        // Content type says PNG but bytes are not a real image
        MultipartFile file = new MockMultipartFile("file", "fake.png", "image/png", "not-an-image".getBytes());
        assertFalse(validator.isValid(file, context));
    }

    private byte[] createValidPngBytes() throws IOException {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return baos.toByteArray();
    }
}
