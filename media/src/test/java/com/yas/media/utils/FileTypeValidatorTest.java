package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class FileTypeValidatorTest {

    private FileTypeValidator fileTypeValidator;
    private ConstraintValidatorContext context;
    private ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        fileTypeValidator = new FileTypeValidator();
        ValidFileType validFileType = mock(ValidFileType.class);
        when(validFileType.allowedTypes()).thenReturn(new String[] {"image/png", "image/jpeg"});
        when(validFileType.message()).thenReturn("Invalid file type");

        fileTypeValidator.initialize(validFileType);

        context = mock(ConstraintValidatorContext.class);
        builder = mock(ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate("Invalid file type")).thenReturn(builder);
    }

    @Test
    void isValid_whenFileIsNull_thenReturnFalse() {
        assertFalse(fileTypeValidator.isValid(null, context));
    }

    @Test
    void isValid_whenContentTypeIsNull_thenReturnFalse() {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", null, new byte[0]);
        assertFalse(fileTypeValidator.isValid(file, context));
    }

    @Test
    void isValid_whenContentTypeIsNotAllowed_thenReturnFalse() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", new byte[0]);
        assertFalse(fileTypeValidator.isValid(file, context));
    }

    @Test
    void isValid_whenFileIsNotImage_thenReturnFalse() {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "not an image".getBytes());
        assertFalse(fileTypeValidator.isValid(file, context));
    }

    @Test
    void isValid_whenFileIsImage_thenReturnTrue() throws IOException {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", imageBytes);
        assertTrue(fileTypeValidator.isValid(file, context));
    }
}
