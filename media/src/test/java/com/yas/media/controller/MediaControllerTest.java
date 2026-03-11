package com.yas.media.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import javax.imageio.ImageIO;
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

    // ── POST /medias ──

    @Test
    void create_whenValidFile_thenReturn200() throws Exception {
        // Create a real 1x1 PNG so FileTypeValidator's ImageIO.read() succeeds
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        byte[] pngBytes = baos.toByteArray();

        MockMultipartFile file = new MockMultipartFile(
            "multipartFile", "photo.png", "image/png", pngBytes
        );

        Media saved = new Media();
        saved.setId(1L);
        saved.setCaption("test caption");
        saved.setFileName("photo.png");
        saved.setMediaType("image/png");

        when(mediaService.saveMedia(any())).thenReturn(saved);

        mockMvc.perform(multipart("/medias")
                .file(file)
                .param("caption", "test caption"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.caption").value("test caption"))
            .andExpect(jsonPath("$.fileName").value("photo.png"));
    }

    // ── DELETE /medias/{id} ──

    @Test
    void delete_whenValidId_thenReturn204() throws Exception {
        doNothing().when(mediaService).removeMedia(1L);

        mockMvc.perform(delete("/medias/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void delete_whenNotFound_thenReturn404() throws Exception {
        doThrow(new NotFoundException("Media 99 is not found"))
            .when(mediaService).removeMedia(99L);

        mockMvc.perform(delete("/medias/99"))
            .andExpect(status().isNotFound());
    }

    // ── GET /medias/{id} ──

    @Test
    void get_whenMediaExists_thenReturn200() throws Exception {
        MediaVm mediaVm = new MediaVm(1L, "caption", "file.png", "image/png", "/medias/1/file/file.png");
        when(mediaService.getMediaById(1L)).thenReturn(mediaVm);

        mockMvc.perform(get("/medias/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.caption").value("caption"));
    }

    @Test
    void get_whenMediaNotFound_thenReturn404() throws Exception {
        when(mediaService.getMediaById(999L)).thenReturn(null);

        mockMvc.perform(get("/medias/999"))
            .andExpect(status().isNotFound());
    }

    // ── GET /medias?ids= ──

    @Test
    void getByIds_whenMediasExist_thenReturn200() throws Exception {
        MediaVm vm1 = new MediaVm(1L, "c1", "f1.png", "image/png", "/url1");
        MediaVm vm2 = new MediaVm(2L, "c2", "f2.png", "image/png", "/url2");
        when(mediaService.getMediaByIds(List.of(1L, 2L))).thenReturn(List.of(vm1, vm2));

        mockMvc.perform(get("/medias").param("ids", "1", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getByIds_whenEmpty_thenReturn404() throws Exception {
        when(mediaService.getMediaByIds(List.of(99L))).thenReturn(List.of());

        mockMvc.perform(get("/medias").param("ids", "99"))
            .andExpect(status().isNotFound());
    }

    // ── GET /medias/{id}/file/{fileName} ──

    @Test
    void getFile_whenExists_thenReturnFileContent() throws Exception {
        byte[] content = "image-bytes".getBytes();
        MediaDto dto = MediaDto.builder()
            .content(new ByteArrayInputStream(content))
            .mediaType(MediaType.IMAGE_PNG)
            .build();

        when(mediaService.getFile(eq(1L), eq("photo.png"))).thenReturn(dto);

        mockMvc.perform(get("/medias/1/file/photo.png"))
            .andExpect(status().isOk());
    }
}
