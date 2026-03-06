package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class FileTypeValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    @InjectMocks
    private FileTypeValidator fileTypeValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock ConstraintValidatorContext behavior
        ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder = mock(
                ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);
    }

    /**
     * Creates a valid PNG image byte array
     */
    private byte[] createValidPngBytes() throws IOException {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, Color.WHITE.getRGB());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", outputStream);
        return outputStream.toByteArray();
    }

    /**
     * Creates a valid JPEG image byte array
     */
    private byte[] createValidJpegBytes() throws IOException {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, Color.WHITE.getRGB());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "JPEG", outputStream);
        return outputStream.toByteArray();
    }

    /**
     * Creates a valid GIF image byte array
     */
    private byte[] createValidGifBytes() throws IOException {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, Color.WHITE.getRGB());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "GIF", outputStream);
        return outputStream.toByteArray();
    }

    @Test
    void isValid_whenFileIsNull_thenReturnFalse() {
        ValidFileType annotation = mock(ValidFileType.class);
        when(annotation.allowedTypes()).thenReturn(new String[] { "image/jpeg", "image/png", "image/gif" });
        when(annotation.message()).thenReturn("Invalid file type");

        fileTypeValidator.initialize(annotation);

        boolean result = fileTypeValidator.isValid(null, context);
        assertFalse(result);
    }

    @Test
    void isValid_whenContentTypeIsNull_thenReturnFalse() {
        ValidFileType annotation = mock(ValidFileType.class);
        when(annotation.allowedTypes()).thenReturn(new String[] { "image/jpeg", "image/png", "image/gif" });
        when(annotation.message()).thenReturn("Invalid file type");

        fileTypeValidator.initialize(annotation);

        MultipartFile file = new MockMultipartFile("file", "test.txt", null, new byte[] {});
        boolean result = fileTypeValidator.isValid(file, context);
        assertFalse(result);
    }

    @Test
    void isValid_whenContentTypeNotAllowed_thenReturnFalse() {
        ValidFileType annotation = mock(ValidFileType.class);
        when(annotation.allowedTypes()).thenReturn(new String[] { "image/jpeg", "image/png", "image/gif" });
        when(annotation.message()).thenReturn("Invalid file type");

        fileTypeValidator.initialize(annotation);

        MultipartFile file = new MockMultipartFile("file", "test.txt", "application/pdf", new byte[] {});
        boolean result = fileTypeValidator.isValid(file, context);
        assertFalse(result);
    }

    @Test
    void isValid_whenValidPngFile_thenReturnTrue() throws IOException {
        ValidFileType annotation = mock(ValidFileType.class);
        when(annotation.allowedTypes()).thenReturn(new String[] { "image/jpeg", "image/png", "image/gif" });
        when(annotation.message()).thenReturn("Invalid file type");

        fileTypeValidator.initialize(annotation);

        byte[] pngBytes = createValidPngBytes();
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", pngBytes);
        boolean result = fileTypeValidator.isValid(file, context);
        assertTrue(result);
    }

    @Test
    void isValid_whenValidJpegFile_thenReturnTrue() throws IOException {
        ValidFileType annotation = mock(ValidFileType.class);
        when(annotation.allowedTypes()).thenReturn(new String[] { "image/jpeg", "image/png", "image/gif" });
        when(annotation.message()).thenReturn("Invalid file type");

        fileTypeValidator.initialize(annotation);

        byte[] jpegBytes = createValidJpegBytes();
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", jpegBytes);
        boolean result = fileTypeValidator.isValid(file, context);
        assertTrue(result);
    }

    @Test
    void isValid_whenValidGifFile_thenReturnTrue() throws IOException {
        ValidFileType annotation = mock(ValidFileType.class);
        when(annotation.allowedTypes()).thenReturn(new String[] { "image/jpeg", "image/png", "image/gif" });
        when(annotation.message()).thenReturn("Invalid file type");

        fileTypeValidator.initialize(annotation);

        byte[] gifBytes = createValidGifBytes();
        MultipartFile file = new MockMultipartFile("file", "test.gif", "image/gif", gifBytes);
        boolean result = fileTypeValidator.isValid(file, context);
        assertTrue(result);
    }

    @Test
    void isValid_whenFileContentIsInvalid_thenReturnFalse() {
        ValidFileType annotation = mock(ValidFileType.class);
        when(annotation.allowedTypes()).thenReturn(new String[] { "image/jpeg", "image/png", "image/gif" });
        when(annotation.message()).thenReturn("Invalid file type");

        fileTypeValidator.initialize(annotation);

        // Invalid image content
        byte[] invalidContent = "This is not a real image".getBytes();
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", invalidContent);
        boolean result = fileTypeValidator.isValid(file, context);
        assertFalse(result);
    }

    @Test
    void isValid_whenGetInputStreamThrowsException_thenReturnFalse() {
        ValidFileType annotation = mock(ValidFileType.class);
        when(annotation.allowedTypes()).thenReturn(new String[] { "image/jpeg", "image/png", "image/gif" });
        when(annotation.message()).thenReturn("Invalid file type");

        fileTypeValidator.initialize(annotation);

        // Create a mock file that throws exception on getInputStream
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getContentType()).thenReturn("image/png");
        try {
            when(mockFile.getInputStream()).thenThrow(new IOException("Stream error"));
        } catch (IOException e) {
            // Won't happen in test
        }

        boolean result = fileTypeValidator.isValid(mockFile, context);
        assertFalse(result);
    }

    @Test
    void isValid_whenMultipleAllowedTypes_thenValidateCorrectly() throws IOException {
        ValidFileType annotation = mock(ValidFileType.class);
        when(annotation.allowedTypes()).thenReturn(new String[] { "image/jpeg", "image/png", "image/gif" });
        when(annotation.message()).thenReturn("Invalid file type");

        fileTypeValidator.initialize(annotation);

        // Valid JPEG
        byte[] jpegBytes = createValidJpegBytes();
        MultipartFile jpegFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", jpegBytes);
        assertTrue(fileTypeValidator.isValid(jpegFile, context));

        // Invalid type (not in allowed list)
        MultipartFile invalidFile = new MockMultipartFile("file", "test.webp", "image/webp", new byte[] {});
        assertFalse(fileTypeValidator.isValid(invalidFile, context));
    }

    @Test
    void isValid_whenEmptyFile_thenReturnFalse() {
        ValidFileType annotation = mock(ValidFileType.class);
        when(annotation.allowedTypes()).thenReturn(new String[] { "image/jpeg", "image/png", "image/gif" });
        when(annotation.message()).thenReturn("Invalid file type");

        fileTypeValidator.initialize(annotation);

        // Empty file with valid content type
        MultipartFile emptyFile = new MockMultipartFile("file", "test.png", "image/png", new byte[] {});
        boolean result = fileTypeValidator.isValid(emptyFile, context);
        assertFalse(result);
    }
}
