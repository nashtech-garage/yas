package com.yas.media.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import jakarta.validation.ConstraintValidatorContext;
import java.awt.image.BufferedImage;
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
    private ValidFileType validFileTypeAnnotation;

    @Mock
    private ConstraintValidatorContext context;
    
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        validator = new FileTypeValidator();
        when(validFileTypeAnnotation.allowedTypes()).thenReturn(new String[]{"image/png", "image/jpeg"});
        when(validFileTypeAnnotation.message()).thenReturn("Invalid file type");
        validator.initialize(validFileTypeAnnotation);

        // Mock context behaviors for invalid cases
        lenient().when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        lenient().when(builder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void isValid_WhenFileIsNull_ShouldReturnFalse() {
        assertThat(validator.isValid(null, context)).isFalse();
    }

    @Test
    void isValid_WhenContentTypeIsNull_ShouldReturnFalse() {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", null, "data".getBytes());
        assertThat(validator.isValid(file, context)).isFalse();
    }

    @Test
    void isValid_WhenContentTypeIsNotAllowed_ShouldReturnFalse() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "data".getBytes());
        assertThat(validator.isValid(file, context)).isFalse();
    }

    @Test
    void isValid_WhenFileIsInvalidImage_ShouldReturnFalse() {
        // Đúng content type nhưng nội dung không phải là ảnh hợp lệ
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "fake-image-data".getBytes());
        assertThat(validator.isValid(file, context)).isFalse();
    }

    @Test
    void isValid_WhenFileIsValidImage_ShouldReturnTrue() throws Exception {
        // Tạo một hình ảnh hợp lệ (1x1 pixel) bằng code để vượt qua ImageIO.read
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", baos.toByteArray());
        
        assertThat(validator.isValid(file, context)).isTrue();
    }
}