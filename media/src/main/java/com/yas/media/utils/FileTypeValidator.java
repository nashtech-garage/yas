package com.yas.media.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;


public class FileTypeValidator implements ConstraintValidator<ValidFileType, MultipartFile> {

    private String[] allowedTypes;
    private String message;

    @Override
    public void initialize(ValidFileType constraintAnnotation) {
        this.allowedTypes = constraintAnnotation.allowedTypes();
        this.message = constraintAnnotation.message();
    }

    @Override
    @SneakyThrows
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.getContentType() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        for (String type : allowedTypes) {
            if (type.equals(file.getContentType())) {
                try {
                    BufferedImage image = ImageIO.read(file.getInputStream());
                    return image != null;
                } catch (IOException e) {
                    return false;
                }
            }
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}
