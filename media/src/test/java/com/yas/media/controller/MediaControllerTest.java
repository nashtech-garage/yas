package com.yas.media.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class MediaControllerTest {

    private MockMvc mockMvc;

    // 1. Giả lập MediaService (vì ta không muốn gọi thật xuống Database)
    @Mock
    private MediaService mediaService;

    // 2. Tiêm cái Service giả đó vào MediaController
    @InjectMocks
    private MediaController mediaController;

    @BeforeEach
    void setUp() {
        // 3. Setup môi trường MockMvc cách ly hoàn toàn
        mockMvc = MockMvcBuilders.standaloneSetup(mediaController).build();
    }

    // --- BẮT ĐẦU VIẾT CÁC HÀM TEST ---

    @Test
    void get_shouldReturnMediaVm_whenMediaExists() throws Exception {
        // Chuẩn bị: Khi ai đó gọi hàm getMediaById(1L) thì trả về một object giả này
        MediaVm mockMediaVm = new MediaVm(1L, "caption", "test.jpg", "image/jpeg", "url");
        when(mediaService.getMediaById(1L)).thenReturn(mockMediaVm);

        // Thực thi & Kiểm tra: Bắn request GET /medias/1 và kỳ vọng trả về status 200 (isOk)
        mockMvc.perform(get("/medias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fileName").value("test.jpg"));
    }

    @Test
    void get_shouldReturn404_whenMediaDoesNotExist() throws Exception {
        // Chuẩn bị: Khi gọi ID số 2, service trả về null (không tìm thấy)
        when(mediaService.getMediaById(2L)).thenReturn(null);

        // Thực thi & Kiểm tra: Bắn request GET /medias/2 và kỳ vọng trả về status 404 (isNotFound)
        mockMvc.perform(get("/medias/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturn204_whenDeleteSuccess() throws Exception {
        // Vì hàm delete trả về void, ta chỉ cần bắn request DELETE và ktra status 204 (isNoContent)
        mockMvc.perform(delete("/medias/1"))
                .andExpect(status().isNoContent());
    }

    // --- 1. Test hàm getByIds (Lấy danh sách) ---
    @Test
    void getByIds_shouldReturnMediaVmList_whenMediasExist() throws Exception {
        // Chuẩn bị: Trả về 2 đối tượng khi được gọi
        MediaVm mediaVm1 = new MediaVm(1L, "caption1", "test1.jpg", "image/jpeg", "url1");
        MediaVm mediaVm2 = new MediaVm(2L, "caption2", "test2.jpg", "image/png", "url2");
        when(mediaService.getMediaByIds(List.of(1L, 2L))).thenReturn(List.of(mediaVm1, mediaVm2));

        // Thực thi: Gọi /medias?ids=1,2
        mockMvc.perform(get("/medias").param("ids", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getByIds_shouldReturn404_whenMediasEmpty() throws Exception {
        // Chuẩn bị: Trả về danh sách rỗng (để test cái dòng if(medias.isEmpty()) màu đỏ của bạn)
        when(mediaService.getMediaByIds(anyList())).thenReturn(List.of());

        // Thực thi
        mockMvc.perform(get("/medias").param("ids", "99"))
                .andExpect(status().isNotFound());
    }

   // --- 2. Test hàm create (Upload File) - Bản vượt ải @ValidFileType ---
    @Test
    void create_shouldReturnNoFileMediaVm_whenValidRequest() throws Exception {
        Media mockMedia = mock(Media.class);
        when(mockMedia.getId()).thenReturn(1L);
        when(mockMedia.getCaption()).thenReturn("Test caption");
        when(mockMedia.getFileName()).thenReturn("image.png");
        when(mockMedia.getMediaType()).thenReturn("image/png");

        when(mediaService.saveMedia(any())).thenReturn(mockMedia);

        // Đây là mảng byte HEX của một bức ảnh PNG thật (1x1 pixel)
        byte[] realPngBytes = new byte[] {
            (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D,
            0x49, 0x48, 0x44, 0x52, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x08,
            0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte)0xC4, (byte)0x89, 0x00, 0x00, 0x00,
            0x0A, 0x49, 0x44, 0x41, 0x54, 0x78, (byte)0x9C, 0x63, 0x00, 0x01, 0x00, 0x00,
            0x05, 0x00, 0x01, 0x0D, 0x0A, 0x2D, (byte)0xB4, 0x00, 0x00, 0x00, 0x00, 0x49,
            0x45, 0x4E, 0x44, (byte)0xAE, 0x42, 0x60, (byte)0x82
        };

        MockMultipartFile file = new MockMultipartFile(
                "multipartFile", "image.png", MediaType.IMAGE_PNG_VALUE, realPngBytes
        );

        mockMvc.perform(multipart("/medias")
                .file(file)
                .param("caption", "Test caption"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    // --- 3. Test hàm getFile (Tải file về) ---
    @Test
    void getFile_shouldReturnFileContent_whenFileExists() throws Exception {
        // Chuẩn bị: Tạo dữ liệu ảo dạng Byte và cấu hình DTO giả
        byte[] content = "test content".getBytes();
        InputStream is = new ByteArrayInputStream(content);
        
        MediaDto mockDto = mock(MediaDto.class);
        when(mockDto.getContent()).thenReturn(is);
        when(mockDto.getMediaType()).thenReturn(MediaType.IMAGE_JPEG);

        when(mediaService.getFile(1L, "test.jpg")).thenReturn(mockDto);

        // Thực thi: Kiểm tra trả về đúng file, đúng header download và đúng định dạng
        mockMvc.perform(get("/medias/1/file/test.jpg"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.jpg\""))
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(content));
    }
}