package com.yas.media.service;

import com.yas.media.config.YasConfig;
import com.yas.media.exception.MultipartFileContentException;
import com.yas.media.exception.NotFoundException;
import com.yas.media.exception.UnsupportedMediaTypeException;
import com.yas.media.model.Media;
import com.yas.media.repository.MediaRepository;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;
import com.yas.media.viewmodel.NoFileMediaVm;
import java.io.IOException;
import java.util.Objects;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final YasConfig yasConfig;

    public MediaServiceImpl(MediaRepository mediaRepository, YasConfig yasConfig) {
        this.mediaRepository = mediaRepository;
        this.yasConfig = yasConfig;
    }

    @Override
    public Media saveMedia(MediaPostVm mediaPostVm) {
        MediaType mediaType = MediaType.valueOf(Objects.requireNonNull(mediaPostVm.multipartFile().getContentType()));
        if (!(MediaType.IMAGE_PNG.equals(mediaType)
            || MediaType.IMAGE_JPEG.equals(mediaType)
            || MediaType.IMAGE_GIF.equals(mediaType))) {
            throw new UnsupportedMediaTypeException();
        }
        Media media = new Media();
        media.setCaption(mediaPostVm.caption());
        media.setMediaType(mediaPostVm.multipartFile().getContentType());

        try {
            media.setData(mediaPostVm.multipartFile().getBytes());
        } catch (IOException e) {
            throw new MultipartFileContentException(e);
        }

        if (mediaPostVm.fileNameOverride() == null || mediaPostVm.fileNameOverride().isEmpty()
            || mediaPostVm.fileNameOverride().trim().isEmpty()) {
            media.setFileName(mediaPostVm.multipartFile().getOriginalFilename());
        } else {
            media.setFileName(mediaPostVm.fileNameOverride());
        }

        return mediaRepository.saveAndFlush(media);
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
}
