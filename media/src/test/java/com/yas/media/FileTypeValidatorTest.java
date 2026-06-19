package com.yas.media;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import com.yas.media.utils.FileTypeValidator;
import com.yas.media.utils.ValidFileType;
import jakarta.validation.ConstraintValidatorContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class FileTypeValidatorTest {

    private FileTypeValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new FileTypeValidator();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        ValidFileType ann = mock(ValidFileType.class);
        when(ann.allowedTypes()).thenReturn(new String[]{"image/png", "image/jpeg"});
        when(ann.message()).thenReturn("Invalid file type");
        validator.initialize(ann);
    }

    @Test
    void nullFile_returnsFalse_andBuildsViolation() {
        assertFalse(validator.isValid(null, context));
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void nullContentType_returnsFalse_andBuildsViolation() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn(null);

        assertFalse(validator.isValid(file, context));
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void unsupportedContentType_returnsFalse_andBuildsViolation() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("text/plain");

        assertFalse(validator.isValid(file, context));
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void validImage_returnsTrue() throws Exception {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        MockMultipartFile file = new MockMultipartFile("file", "img.png", "image/png", baos.toByteArray());

        assertTrue(validator.isValid(file, context));
    }

    @Test
    void ioExceptionDuringRead_returnsFalse() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getInputStream()).thenThrow(new java.io.IOException("iofail"));

        assertFalse(validator.isValid(file, context));
    }
}
