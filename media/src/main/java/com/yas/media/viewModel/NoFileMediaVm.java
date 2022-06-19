package com.yas.media.viewModel;

public record NoFileMediaVm(Long id, String caption, String fileName, String mediaType){
public String getUrl() {
    return String.format("/medias/%1$s/file/%2$s", id, fileName);
    }
}
