package com.yas.media.viewmodel;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record MediaPostVm(String caption, @NotNull MultipartFile multipartFile, String fileNameOverride) {
}
