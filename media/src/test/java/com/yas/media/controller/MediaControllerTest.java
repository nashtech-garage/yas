package com.yas.media.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.service.MediaService;
import com.yas.media.viewmodel.MediaVm;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    void create_whenValidRequest_thenReturn200() throws Exception {
        Media media = new Media();
        media.setId(1L);
        media.setCaption("caption");
        media.setFileName("test.png");
        media.setMediaType("image/png");
        when(mediaService.saveMedia(any())).thenReturn(media);

        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "PNG", baos);
        MockMultipartFile file = new MockMultipartFile(
            "multipartFile", "test.png", "image/png", baos.toByteArray());

        mockMvc.perform(multipart("/medias")
                .file(file)
                .param("caption", "caption"))
            .andExpect(status().isOk());
    }

    @Test
    void delete_whenValidId_thenReturn204() throws Exception {
        doNothing().when(mediaService).removeMedia(1L);

        mockMvc.perform(delete("/medias/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void get_whenMediaExists_thenReturn200() throws Exception {
        MediaVm mediaVm = new MediaVm(1L, "caption", "test.png", "image/png", "http://url");
        when(mediaService.getMediaById(1L)).thenReturn(mediaVm);

        mockMvc.perform(get("/medias/1"))
            .andExpect(status().isOk());
    }

    @Test
    void get_whenMediaNotFound_thenReturn404() throws Exception {
        when(mediaService.getMediaById(1L)).thenReturn(null);

        mockMvc.perform(get("/medias/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getByIds_whenMediasExist_thenReturn200() throws Exception {
        List<MediaVm> mediaVms = List.of(
            new MediaVm(1L, "caption", "test.png", "image/png", "http://url")
        );
        when(mediaService.getMediaByIds(List.of(1L))).thenReturn(mediaVms);

        mockMvc.perform(get("/medias").param("ids", "1"))
            .andExpect(status().isOk());
    }

    @Test
    void getByIds_whenNoMediaFound_thenReturn404() throws Exception {
        when(mediaService.getMediaByIds(List.of(1L))).thenReturn(List.of());

        mockMvc.perform(get("/medias").param("ids", "1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getFile_whenValidRequest_thenReturn200() throws Exception {
        MediaDto mediaDto = MediaDto.builder()
            .content(new ByteArrayInputStream(new byte[]{1, 2, 3}))
            .mediaType(MediaType.IMAGE_PNG)
            .build();
        when(mediaService.getFile(1L, "test.png")).thenReturn(mediaDto);

        mockMvc.perform(get("/medias/1/file/test.png"))
            .andExpect(status().isOk());
    }

    @Test
    void getFile_whenMediaNotFound_thenReturn404() throws Exception {
        when(mediaService.getFile(1L, "missing.png")).thenReturn(MediaDto.builder().build());

        mockMvc.perform(get("/medias/1/file/missing.png"))
            .andExpect(status().isNotFound());
    }
}
