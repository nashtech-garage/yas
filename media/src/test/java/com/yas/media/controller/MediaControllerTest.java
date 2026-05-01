package com.yas.media.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.service.MediaService;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;
import java.io.ByteArrayInputStream;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class MediaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private MediaController mediaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mediaController).build();
    }

    @Test
    void create_whenValidRequest_thenReturnNoFileMediaVm() throws Exception {
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        MockMultipartFile file = new MockMultipartFile("multipartFile", "test.png", "image/png", imageBytes);
        Media media = new Media();
        media.setId(1L);
        media.setCaption("test caption");
        media.setFileName("test.png");
        media.setMediaType("image/png");

        when(mediaService.saveMedia(any(MediaPostVm.class))).thenReturn(media);

        mockMvc.perform(multipart("/medias")
                .file(file)
                .param("caption", "test caption")
                .param("fileName", "test.png")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.caption").value("test caption"))
            .andExpect(jsonPath("$.fileName").value("test.png"))
            .andExpect(jsonPath("$.mediaType").value("image/png"));
    }

    @Test
    void delete_whenValidId_thenReturnNoContent() throws Exception {
        doNothing().when(mediaService).removeMedia(1L);

        mockMvc.perform(delete("/medias/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void get_whenMediaExists_thenReturnMediaVm() throws Exception {
        MediaVm mediaVm = new MediaVm(1L, "test caption", "test.png", "image/png", "/url");
        when(mediaService.getMediaById(1L)).thenReturn(mediaVm);

        mockMvc.perform(get("/medias/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.caption").value("test caption"))
            .andExpect(jsonPath("$.fileName").value("test.png"))
            .andExpect(jsonPath("$.mediaType").value("image/png"))
            .andExpect(jsonPath("$.url").value("/url"));
    }

    @Test
    void get_whenMediaNotFound_thenReturnNotFound() throws Exception {
        when(mediaService.getMediaById(1L)).thenReturn(null);

        mockMvc.perform(get("/medias/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getByIds_whenMediasExist_thenReturnListMediaVm() throws Exception {
        MediaVm mediaVm1 = new MediaVm(1L, "test caption 1", "test1.png", "image/png", "/url1");
        MediaVm mediaVm2 = new MediaVm(2L, "test caption 2", "test2.png", "image/png", "/url2");
        when(mediaService.getMediaByIds(List.of(1L, 2L))).thenReturn(List.of(mediaVm1, mediaVm2));

        mockMvc.perform(get("/medias")
                .param("ids", "1,2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void getByIds_whenMediasNotFound_thenReturnNotFound() throws Exception {
        when(mediaService.getMediaByIds(List.of(1L, 2L))).thenReturn(List.of());

        mockMvc.perform(get("/medias")
                .param("ids", "1,2"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getFile_whenValidRequest_thenReturnInputStreamResource() throws Exception {
        byte[] contentBytes = "test content".getBytes();
        MediaDto mediaDto = MediaDto.builder()
            .content(new ByteArrayInputStream(contentBytes))
            .mediaType(MediaType.IMAGE_PNG)
            .build();

        when(mediaService.getFile(1L, "test.png")).thenReturn(mediaDto);

        mockMvc.perform(get("/medias/1/file/test.png"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.png\""))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE))
            .andExpect(content().bytes(contentBytes));
    }
}
