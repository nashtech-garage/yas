package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.media.utils.ValidFileType;
import jakarta.validation.ConstraintValidatorContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

class FileTypeValidatorTest {

    private FileTypeValidator validator;
    private ValidFileType annotation;
    private ConstraintValidatorContext context;
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        validator = new FileTypeValidator();
        annotation = mock(ValidFileType.class);
        context = mock(ConstraintValidatorContext.class);
        violationBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        when(annotation.allowedTypes()).thenReturn(new String[]{"image/png", "image/jpeg"});
        when(annotation.message()).thenReturn("Invalid file type");
        when(context.buildConstraintViolationWithTemplate("Invalid file type")).thenReturn(violationBuilder);

        validator.initialize(annotation);
    }

    @Test
    void isValid_whenFileIsNull_thenReturnFalse() {
        assertFalse(validator.isValid(null, context));
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void isValid_whenContentTypeIsNull_thenReturnFalse() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn(null);

        assertFalse(validator.isValid(file, context));
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void isValid_whenTypeNotAllowed_thenReturnFalse() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("application/pdf");

        assertFalse(validator.isValid(file, context));
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void isValid_whenValidPngFileButEmptyContent_thenReturnFalse() throws IOException {
        // Empty byte array is not a valid PNG image, so ImageIO.read returns null → false
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

        assertFalse(validator.isValid(file, context));
    }

    @Test
    void isValid_whenInputStreamThrowsIOException_thenReturnFalse() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/png");
        InputStream errorStream = mock(InputStream.class);
        when(file.getInputStream()).thenReturn(errorStream);
        when(errorStream.read()).thenThrow(new IOException("IO error"));

        assertFalse(validator.isValid(file, context));
    }
}
