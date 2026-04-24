package com.yas.media.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.service.MediaService;
import com.yas.media.viewmodel.MediaVm;
import com.yas.media.viewmodel.NoFileMediaVm;
import java.io.ByteArrayInputStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MediaController.class)
@AutoConfigureMockMvc(addSpringSecurityAutoConfiguration = false)
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MediaService mediaService;

    @Test
    void create_whenValidInput_thenReturnOk() throws Exception {
        Media media = new Media();
        media.setId(1L);
        media.setCaption("test");
        media.setFileName("test.png");
        media.setMediaType("image/png");

        when(mediaService.saveMedia(any())).thenReturn(media);

        MockMultipartFile file = new MockMultipartFile(
            "file", "test.png", "image/png", "content".getBytes()
        );

        mockMvc.perform(multipart("/medias")
                .file(file)
                .param("caption", "test")
                .param("fileName", "test.png")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.caption").value("test"))
            .andExpect(jsonPath("$.fileName").value("test.png"));
    }

    @Test
    void delete_whenValidId_thenReturnNoContent() throws Exception {
        doNothing().when(mediaService).removeMedia(1L);

        mockMvc.perform(delete("/medias/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void delete_whenMediaNotFound_thenReturnNotFound() throws Exception {
        doThrow(new NotFoundException("Media 99 is not found"))
            .when(mediaService).removeMedia(99L);

        mockMvc.perform(delete("/medias/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void get_whenValidId_thenReturnMediaVm() throws Exception {
        MediaVm mediaVm = new MediaVm(1L, "caption", "test.png", "image/png", "/media/medias/1/file/test.png");
        when(mediaService.getMediaById(1L)).thenReturn(mediaVm);

        mockMvc.perform(get("/medias/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.caption").value("caption"));
    }

    @Test
    void get_whenMediaNotFound_thenReturnNotFound() throws Exception {
        when(mediaService.getMediaById(99L)).thenReturn(null);

        mockMvc.perform(get("/medias/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getByIds_whenMediasExist_thenReturnList() throws Exception {
        MediaVm mediaVm = new MediaVm(1L, "caption", "test.png", "image/png", "/url");
        when(mediaService.getMediaByIds(List.of(1L, 2L))).thenReturn(List.of(mediaVm));

        mockMvc.perform(get("/medias")
                .param("ids", "1", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getByIds_whenNoMediaFound_thenReturnNotFound() throws Exception {
        when(mediaService.getMediaByIds(any())).thenReturn(List.of());

        mockMvc.perform(get("/medias")
                .param("ids", "99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getFile_whenValidIdAndName_thenReturnFile() throws Exception {
        MediaDto mediaDto = MediaDto.builder()
            .content(new ByteArrayInputStream("file-content".getBytes()))
            .mediaType(org.springframework.http.MediaType.IMAGE_PNG)
            .build();
        when(mediaService.getFile(anyLong(), anyString())).thenReturn(mediaDto);

        mockMvc.perform(get("/medias/1/file/test.png"))
            .andExpect(status().isOk());
    }
}
