package com.yas.media.viewmodel;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

public record MediaPostVm(String caption, @NotNull MultipartFile multipartFile, String fileNameOverride) {
}
