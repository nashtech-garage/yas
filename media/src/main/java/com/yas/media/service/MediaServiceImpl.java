package com.yas.media.service;

import com.yas.media.config.YasConfig;
import com.yas.media.exception.MultipartFileContentException;
import com.yas.media.exception.NotFoundException;
import com.yas.media.exception.UnsupportedMediaTypeException;
import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.model.dto.MediaDto.MediaDtoBuilder;
import com.yas.media.repository.FileSystemRepository;
import com.yas.media.repository.MediaRepository;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;
import com.yas.media.viewmodel.NoFileMediaVm;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Service
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final FileSystemRepository fileSystemRepository;
    private final YasConfig yasConfig;

    @Override
    public Media saveMedia(MediaPostVm mediaPostVm) {
        MediaType mediaType = MediaType.valueOf(Objects.requireNonNull(mediaPostVm.multipartFile().getContentType()));
        if (!(MediaType.IMAGE_PNG.equals(mediaType) || MediaType.IMAGE_JPEG.equals(mediaType)
            || MediaType.IMAGE_GIF.equals(mediaType))) {
            throw new UnsupportedMediaTypeException();
        }
        Media media = new Media();
        media.setCaption(mediaPostVm.caption());
        media.setMediaType(mediaPostVm.multipartFile().getContentType());

        if (StringUtils.hasText(mediaPostVm.fileNameOverride().trim())) {
            media.setFileName(mediaPostVm.fileNameOverride());
        } else {
            media.setFileName(mediaPostVm.multipartFile().getOriginalFilename());
        }
        try {
            String filePath = fileSystemRepository.persistFile(media.getFileName(),
                mediaPostVm.multipartFile().getBytes());
            media.setFilePath(filePath);
        } catch (IOException e) {
            throw new MultipartFileContentException(e);
        }
        return mediaRepository.save(media);
    }

    @Override
    public void removeMedia(Long id) {
        NoFileMediaVm noFileMediaVm = mediaRepository.findByIdWithoutFileInReturn(id);
        if (noFileMediaVm == null) {
            throw new NotFoundException(String.format("Media %s is not found", id));
        }
        mediaRepository.deleteById(id);
    }

    @Override
    public MediaVm getMediaById(Long id) {
        NoFileMediaVm noFileMediaVm = mediaRepository.findByIdWithoutFileInReturn(id);
        if (noFileMediaVm == null) {
            return null;
        }
        String url = UriComponentsBuilder.fromUriString(yasConfig.publicUrl())
            .path(String.format("/medias/%1$s/file/%2$s", noFileMediaVm.id(), noFileMediaVm.fileName()))
            .build().toUriString();

        return new MediaVm(
            noFileMediaVm.id(),
            noFileMediaVm.caption(),
            noFileMediaVm.fileName(),
            noFileMediaVm.mediaType(),
            url
        );
    }

    @Override
    public MediaDto getFile(Long id, String fileName) {

        MediaDtoBuilder builder = MediaDto.builder();

        Media media = mediaRepository.findById(id).orElse(null);
        if (media == null || !fileName.equalsIgnoreCase(media.getFileName())) {
            return builder.build();
        }
        MediaType mediaType = MediaType.valueOf(media.getMediaType());
        byte[] fileContent = fileSystemRepository.getFile(media.getFilePath());

        return builder
            .content(fileContent)
            .mediaType(mediaType)
            .build();
    }
}
