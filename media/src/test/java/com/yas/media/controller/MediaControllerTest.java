package com.yas.media.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.service.MediaService;
import com.yas.media.viewmodel.MediaVm;
import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MediaController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MediaService mediaService;

    // ========== GET /medias/{id} ==========

    @Test
    void getMedia_whenValidId_thenReturn200WithMediaVm() throws Exception {
        MediaVm mediaVm = new MediaVm(1L, "Test caption", "photo.png", "image/png", "/medias/1/file/photo.png");
        when(mediaService.getMediaById(1L)).thenReturn(mediaVm);

        mockMvc.perform(get("/medias/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.caption").value("Test caption"))
            .andExpect(jsonPath("$.fileName").value("photo.png"))
            .andExpect(jsonPath("$.mediaType").value("image/png"))
            .andExpect(jsonPath("$.url").value("/medias/1/file/photo.png"));
    }

    @Test
    void getMedia_whenMediaNotFound_thenReturn404() throws Exception {
        when(mediaService.getMediaById(1L)).thenReturn(null);

        mockMvc.perform(get("/medias/1"))
            .andExpect(status().isNotFound());
    }

    // ========== GET /medias?ids= ==========

    @Test
    void getMediaByIds_whenValidIds_thenReturn200WithList() throws Exception {
        List<MediaVm> mediaList = List.of(
            new MediaVm(1L, "Caption 1", "file1.png", "image/png", "/medias/1/file/file1.png"),
            new MediaVm(2L, "Caption 2", "file2.jpg", "image/jpeg", "/medias/2/file/file2.jpg")
        );
        when(mediaService.getMediaByIds(List.of(1L, 2L))).thenReturn(mediaList);

        mockMvc.perform(get("/medias")
                .param("ids", "1", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].caption").value("Caption 1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].caption").value("Caption 2"));
    }

    @Test
    void getMediaByIds_whenNoMediaFound_thenReturn404() throws Exception {
        when(mediaService.getMediaByIds(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/medias")
                .param("ids", "999"))
            .andExpect(status().isNotFound());
    }

    // ========== POST /medias ==========

    @Test
    void createMedia_whenValidInput_thenReturn200WithNoFileMediaVm() throws Exception {
        Media savedMedia = new Media();
        savedMedia.setId(1L);
        savedMedia.setCaption("New media");
        savedMedia.setFileName("upload.png");
        savedMedia.setMediaType("image/png");

        when(mediaService.saveMedia(any())).thenReturn(savedMedia);

        // Create a real PNG image to pass @ValidFileType validation
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);

        MockMultipartFile multipartFile = new MockMultipartFile(
            "multipartFile", "upload.png", "image/png", baos.toByteArray()
        );

        mockMvc.perform(multipart("/medias")
                .file(multipartFile)
                .param("caption", "New media"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.caption").value("New media"))
            .andExpect(jsonPath("$.fileName").value("upload.png"))
            .andExpect(jsonPath("$.mediaType").value("image/png"));
    }

    // ========== DELETE /medias/{id} ==========

    @Test
    void deleteMedia_whenValidId_thenReturn204() throws Exception {
        doNothing().when(mediaService).removeMedia(1L);

        mockMvc.perform(delete("/medias/1"))
            .andExpect(status().isNoContent());

        verify(mediaService).removeMedia(1L);
    }

    @Test
    void deleteMedia_whenMediaNotFound_thenThrowNotFoundException() throws Exception {
        doThrow(new NotFoundException("Media 999 is not found"))
            .when(mediaService).removeMedia(999L);

        mockMvc.perform(delete("/medias/999"))
            .andExpect(status().isNotFound());
    }

    // ========== GET /medias/{id}/file/{fileName} ==========

    @Test
    void getFile_whenValidIdAndFileName_thenReturnFileContent() throws Exception {
        byte[] fileContent = "image-binary-data".getBytes();
        MediaDto mediaDto = MediaDto.builder()
            .content(new ByteArrayInputStream(fileContent))
            .mediaType(MediaType.IMAGE_PNG)
            .build();

        when(mediaService.getFile(1L, "photo.png")).thenReturn(mediaDto);

        mockMvc.perform(get("/medias/1/file/photo.png"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.IMAGE_PNG))
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"photo.png\""));
    }
}
