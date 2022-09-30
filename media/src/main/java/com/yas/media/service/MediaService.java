package com.yas.media.service;

import com.yas.media.model.Media;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;

public interface MediaService {
    Media saveMedia(MediaPostVm mediaPostVm);

    MediaVm getMediaById(Long id);
    
    void removeMedia(Long id);
}
