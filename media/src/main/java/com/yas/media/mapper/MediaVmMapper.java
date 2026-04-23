package com.yas.media.mapper;

import com.yas.commonlibrary.mapper.BaseMapper;
import com.yas.media.model.Media;
import com.yas.media.viewmodel.MediaVm;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MediaVmMapper extends BaseMapper<Media, MediaVm> {
}
