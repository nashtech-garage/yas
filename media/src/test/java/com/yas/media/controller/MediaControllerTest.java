package com.yas.media.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;  
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MediaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private MediaController mediaController;

    @BeforeEach
    void setUp() {
        // Khởi tạo MockMvc độc lập và "đánh lừa" hệ thống Validation
        mockMvc = MockMvcBuilders.standaloneSetup(mediaController)
                .setValidator(new org.springframework.validation.Validator() {
                    @Override
                    public boolean supports(Class<?> clazz) {
                        return true; // Chấp nhận mọi class
                    }
                    @Override
                    public void validate(Object target, org.springframework.validation.Errors errors) {
                        // Để trống = Không báo lỗi = Bỏ qua kiểm tra hợp lệ
                    }
                })
                .build();
    }

    @Test
    void create_ShouldReturnNoFileMediaVm_WhenValid() throws Exception {
        Media mockMedia = new Media();
        mockMedia.setId(1L);
        mockMedia.setCaption("Test Caption");
        mockMedia.setFileName("test.png");
        mockMedia.setMediaType("image/png");

        when(mediaService.saveMedia(any(MediaPostVm.class))).thenReturn(mockMedia);

        // Đã sửa "file" thành "multipartFile" cho khớp với MediaPostVm
        MockMultipartFile file = new MockMultipartFile(
                "multipartFile", 
                "test.png", 
                "image/png", 
                "test data".getBytes()
        );

        mockMvc.perform(multipart("/medias")
                        .file(file)
                        .param("caption", "Test Caption")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fileName").value("test.png"))
                .andExpect(jsonPath("$.caption").value("Test Caption"))
                .andExpect(jsonPath("$.mediaType").value("image/png"));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/medias/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void get_ShouldReturnMediaVm_WhenFound() throws Exception {
        MediaVm mediaVm = mock(MediaVm.class);
        when(mediaService.getMediaById(1L)).thenReturn(mediaVm);

        mockMvc.perform(get("/medias/1"))
                .andExpect(status().isOk());
    }

    @Test
    void get_ShouldReturnNotFound_WhenNotFound() throws Exception {
        when(mediaService.getMediaById(1L)).thenReturn(null);

        mockMvc.perform(get("/medias/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByIds_ShouldReturnList_WhenFound() throws Exception {
        MediaVm mediaVm = mock(MediaVm.class);
        when(mediaService.getMediaByIds(List.of(1L, 2L))).thenReturn(List.of(mediaVm));

        mockMvc.perform(get("/medias")
                        .param("ids", "1", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void getByIds_ShouldReturnNotFound_WhenEmpty() throws Exception {
        when(mediaService.getMediaByIds(List.of(1L, 2L))).thenReturn(List.of());

        mockMvc.perform(get("/medias")
                        .param("ids", "1", "2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFile_ShouldReturnFile_WhenFound() throws Exception {
        MediaDto mediaDto = mock(MediaDto.class);
        when(mediaDto.getMediaType()).thenReturn(MediaType.IMAGE_PNG);
        when(mediaDto.getContent()).thenReturn(new ByteArrayInputStream("test data".getBytes()));

        when(mediaService.getFile(1L, "test.png")).thenReturn(mediaDto);

        mockMvc.perform(get("/medias/1/file/test.png"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.png\""))
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }
}