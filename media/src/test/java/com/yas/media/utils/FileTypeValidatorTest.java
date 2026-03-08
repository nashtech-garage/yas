package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class FileTypeValidatorTest {

    private FileTypeValidator validator;
    private ConstraintValidatorContext context;
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        validator = new FileTypeValidator();
        ValidFileType validFileType = mock(ValidFileType.class);
        when(validFileType.allowedTypes()).thenReturn(new String[]{"image/png", "image/jpeg"});
        when(validFileType.message()).thenReturn("Invalid file type");
        
        validator.initialize(validFileType);

        context = mock(ConstraintValidatorContext.class);
        builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    void isValid_whenFileIsNull_thenReturnFalse() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    void isValid_whenContentTypeIsNull_thenReturnFalse() {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", null, new byte[]{});
        assertFalse(validator.isValid(file, context));
    }

    @Test
    void isValid_whenContentTypeNotAllowed_thenReturnFalse() {
        MockMultipartFile file = new MockMultipartFile("file", "test.gif", "image/gif", new byte[]{});
        assertFalse(validator.isValid(file, context));
    }

    @Test
    void isValid_whenFileIsInvalidImage_thenReturnFalse() {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", new byte[]{1, 2, 3});
        assertFalse(validator.isValid(file, context));
    }

    @Test
    void isValid_whenFileIsRealImage_thenReturnTrue() throws Exception {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(img, "png", baos);
        byte[] bytes = baos.toByteArray();

        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", bytes);
        assertTrue(validator.isValid(file, context));
    }
}
