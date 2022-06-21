package com.yas.media.service;

import com.yas.media.model.Media;
import com.yas.media.viewmodel.MediaPostVm;

public interface MediaService {
    Media saveMedia(MediaPostVm mediaPostVm);
}
