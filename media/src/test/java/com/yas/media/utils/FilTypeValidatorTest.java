package com.yas.media.utils;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class FileTypeValidatorTest {

    private FileTypeValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new FileTypeValidator();
        
        ValidFileType annotation = mock(ValidFileType.class);
        when(annotation.allowedTypes()).thenReturn(new String[]{"image/jpeg", "image/png"});
        when(annotation.message()).thenReturn("Invalid type");
        
        validator.initialize(annotation);

        context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    void testIsValid_NullFile() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    void testIsValid_NullContentType() {
        MultipartFile file = new MockMultipartFile("file", "test.png", null, new byte[0]);
        assertFalse(validator.isValid(file, context));
    }

    @Test
    void testIsValid_NotAllowedType() {
        MultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[0]);
        assertFalse(validator.isValid(file, context));
    }

    @Test
    void testIsValid_AllowedTypeButNotAnImage() {
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "fake image data".getBytes());
        assertFalse(validator.isValid(file, context));
    }

    @Test
    void testIsValid_ThrowIOException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getInputStream()).thenThrow(new IOException("Test exception"));
        
        assertFalse(validator.isValid(file, context));
    }

    @Test
    void testIsValid_Success() {
        String base64Png = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==";
        byte[] imageBytes = Base64.getDecoder().decode(base64Png);
        
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", imageBytes);

        assertTrue(validator.isValid(file, context));
    }
}