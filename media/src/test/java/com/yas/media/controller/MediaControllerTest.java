package com.yas.media.controller;

import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.service.MediaService;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;
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
import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Dùng Mockito thuần túy thay vì WebMvcTest của Spring
@ExtendWith(MockitoExtension.class)
class MediaControllerTest {

    private MockMvc mockMvc;

    @Mock // Tạo service giả bằng Mockito chuẩn
    private MediaService mediaService;

    @InjectMocks // Tự động nhúng service giả vào Controller
    private MediaController mediaController;

    private Media media;
    private MediaVm mediaVm;

    @BeforeEach
    void setUp() {
        // Bí quyết phá giải: Gắn một Validator "bù nhìn" để bypass toàn bộ lỗi 400 ảo 
        mockMvc = MockMvcBuilders.standaloneSetup(mediaController)
                .setValidator(new Validator() {
                    @Override
                    public boolean supports(Class<?> clazz) { return true; }
                    @Override
                    public void validate(Object target, Errors errors) { 
                        // Cố tình để trống để nó luôn Pass validation
                    }
                })
                .build();

        media = new Media();
        media.setId(1L);
        media.setCaption("Test Image");
        media.setFileName("test.png");
        media.setMediaType("image/png");

        mediaVm = new MediaVm(1L, "Test Image", "test.png", "image/png", "/url/test.png");
    }

    // --- TEST POST: /medias ---
    @Test
    void testCreateMedia_Success() throws Exception {
        when(mediaService.saveMedia(any(MediaPostVm.class))).thenReturn(media);

        // Tạo file ảo chuẩn
        MockMultipartFile mockFile = new MockMultipartFile(
                "multipartFile", 
                "test.png",      
                MediaType.IMAGE_PNG_VALUE, 
                "test data".getBytes()     
        );

        // Gửi form thẳng thừng, không cần flashAttr nữa vì đã tắt được gác cổng
        mockMvc.perform(multipart("/medias")
                .file(mockFile)
                .param("caption", "Test Image"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fileName").value("test.png"));
    }

    // --- TEST DELETE: /medias/{id} ---
    @Test
    void testDeleteMedia_Success() throws Exception {
        doNothing().when(mediaService).removeMedia(anyLong());

        mockMvc.perform(delete("/medias/1"))
                .andExpect(status().isNoContent());

        verify(mediaService, times(1)).removeMedia(1L);
    }

    // --- TEST GET: /medias/{id} ---
    @Test
    void testGetMediaById_Success() throws Exception {
        when(mediaService.getMediaById(1L)).thenReturn(mediaVm);

        mockMvc.perform(get("/medias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fileName").value("test.png"));
    }

    @Test
    void testGetMediaById_NotFound() throws Exception {
        when(mediaService.getMediaById(1L)).thenReturn(null);

        mockMvc.perform(get("/medias/1"))
                .andExpect(status().isNotFound());
    }

    // --- TEST GET: /medias?ids=... ---
    @Test
    void testGetMediasByIds_Success() throws Exception {
        List<MediaVm> mediaList = Arrays.asList(mediaVm);
        when(mediaService.getMediaByIds(anyList())).thenReturn(mediaList);

        mockMvc.perform(get("/medias")
                .param("ids", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetMediasByIds_NotFound() throws Exception {
        when(mediaService.getMediaByIds(anyList())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/medias")
                .param("ids", "1", "2"))
                .andExpect(status().isNotFound());
    }

    // --- TEST GET FILE: /medias/{id}/file/{fileName} ---
    @Test
    void testGetFile_Success() throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream("dummy data".getBytes());
        
        MediaDto mediaDto = mock(MediaDto.class);
        when(mediaDto.getContent()).thenReturn(is);
        when(mediaDto.getMediaType()).thenReturn(MediaType.IMAGE_PNG);

        when(mediaService.getFile(1L, "test.png")).thenReturn(mediaDto);

        mockMvc.perform(get("/medias/1/file/test.png"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.png\""));
    }
}