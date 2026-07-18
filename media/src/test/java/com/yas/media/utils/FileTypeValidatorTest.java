package com.yas.media.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class FileTypeValidatorTest {

    private FileTypeValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        validator = new FileTypeValidator();

        ValidFileType annotation = mock(ValidFileType.class);
        when(annotation.allowedTypes()).thenReturn(new String[]{"image/jpeg", "image/png"});
        when(annotation.message()).thenReturn("Invalid file type");

        validator.initialize(annotation);
    }

    @Test
    void isValid_shouldReturnFalse_whenFileIsNull() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);

        boolean result = validator.isValid(null, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void isValid_shouldReturnFalse_whenContentTypeIsNull() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", null, new byte[]{});
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);

        boolean result = validator.isValid(file, context);

        assertThat(result).isFalse();
    }

    @Test
    void isValid_shouldReturnFalse_whenContentTypeNotAllowed() {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.pdf", "application/pdf", new byte[]{1});
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);

        boolean result = validator.isValid(file, context);

        assertThat(result).isFalse();
    }

    @Test
    void isValid_shouldReturnTrue_whenValidJpegImage() throws Exception {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpeg", baos);
        byte[] imageBytes = baos.toByteArray();

        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", imageBytes);

        boolean result = validator.isValid(file, context);

        assertThat(result).isTrue();
    }

    @Test
    void isValid_shouldReturnFalse_whenContentTypeMatchesButNotRealImage() {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", new byte[]{1, 2, 3});

        boolean result = validator.isValid(file, context);

        assertThat(result).isFalse();
    }
}
