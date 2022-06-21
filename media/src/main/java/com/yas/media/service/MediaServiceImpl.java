package com.yas.media.service;

import com.yas.media.exception.MultipartFileContentException;
import com.yas.media.exception.UnsupportedMediaTypeException;
import com.yas.media.model.Media;
import com.yas.media.repository.MediaRepository;
import com.yas.media.viewModel.MediaPostVm;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;

    public MediaServiceImpl(MediaRepository mediaRepository){
        this.mediaRepository = mediaRepository;
    }

    @Override
    public Media saveMedia(MediaPostVm mediaPostVm) {
        MediaType mediaType = MediaType.valueOf(mediaPostVm.multipartFile().getContentType());
        if(!(MediaType.IMAGE_PNG.equals(mediaType) || MediaType.IMAGE_JPEG.equals(mediaType) || MediaType.IMAGE_GIF.equals(mediaType))){
            throw new UnsupportedMediaTypeException();
        }
        Media media = new Media();
        media.setCaption(mediaPostVm.caption());
        media.setMediaType(mediaPostVm.multipartFile().getContentType());
        try {
            media.setData(mediaPostVm.multipartFile().getBytes());
        }
        catch (IOException e){
            throw new MultipartFileContentException(e);
        }
        if (mediaPostVm.fileNameOverride() == null || mediaPostVm.fileNameOverride().isEmpty() || mediaPostVm.fileNameOverride().trim().isEmpty()){
            media.setFileName(mediaPostVm.multipartFile().getOriginalFilename());
        }
        else {
            media.setFileName(mediaPostVm.fileNameOverride());
        }

        mediaRepository.saveAndFlush(media);
        return media;
    }
}
