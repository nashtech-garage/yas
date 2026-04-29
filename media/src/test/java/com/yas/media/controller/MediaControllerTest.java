package com.yas.media.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.commonlibrary.exception.ApiExceptionHandler;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.service.MediaService;
import com.yas.media.viewmodel.MediaVm;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
// Spring Boot 4.x: @WebMvcTest and @AutoConfigureMockMvc have moved to a new package.
// They are no longer located in org.springframework.boot.test.autoconfigure.web.servlet.
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit tests for {@link MediaController}.
 *
 * <p>Uses {@code @WebMvcTest} to load only the web layer (controller, filters,
 * validation), without bootstrapping the full Spring application context.
 * {@code MockMvc} simulates HTTP requests and responses without starting a real server.
 *
 * <p>Conventions used in this project (Spring Boot 4.x):
 * <ul>
 *   <li>{@code @WebMvcTest + excludeAutoConfiguration}: removes OAuth2 security
 *       from the test context to avoid authentication setup.</li>
 *   <li>{@code @ContextConfiguration}: loads only the target controller and the
 *       global exception handler.</li>
 *   <li>{@code @AutoConfigureMockMvc(addFilters = false)}: disables the security
 *       filter chain so tests are not blocked by authentication.</li>
 * </ul>
 */
@WebMvcTest(excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@ContextConfiguration(classes = {
    MediaController.class,
    ApiExceptionHandler.class
})
@AutoConfigureMockMvc(addFilters = false)
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MediaService mediaService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== POST /medias ====================

    /**
     * Verifies that uploading a valid PNG file returns 200 OK with the saved media metadata.
     *
     * <p>{@code MockMultipartFile} simulates a multipart/form-data upload from a client.
     * {@code mediaService.saveMedia()} is mocked to return a pre-built {@link Media} object,
     * which the controller wraps into a {@code NoFileMediaVm} (without file content) in the response.
     */
    @Test
    void create_whenValidPngFile_thenReturnOk() throws Exception {
        // Given
        Media savedMedia = new Media();
        savedMedia.setId(1L);
        savedMedia.setCaption("test caption");
        savedMedia.setFileName("photo.png");
        savedMedia.setMediaType("image/png");

        when(mediaService.saveMedia(any())).thenReturn(savedMedia);

        // Generate a real 1x1 PNG to pass FileTypeValidator
        byte[] minimalPng = createMinimalPngBytes();
        MockMultipartFile file = new MockMultipartFile(
            "multipartFile", "photo.png", "image/png", minimalPng
        );

        // When & Then
        MvcResult result = mockMvc.perform(multipart("/medias")
                .file(file)
                .param("caption", "test caption")
                .param("fileNameOverride", "photo.png")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertAll(
            () -> assertEquals(200, result.getResponse().getStatus()),
            () -> assertEquals(1L, body.get("id").asLong()),
            () -> assertEquals("test caption", body.get("caption").asText()),
            () -> assertEquals("photo.png", body.get("fileName").asText())
        );
    }

    // ==================== DELETE /medias/{id} ====================

    /**
     * Verifies that deleting an existing media resource returns 204 No Content.
     */
    @Test
    void delete_whenMediaExists_thenReturn204() throws Exception {
        doNothing().when(mediaService).removeMedia(1L);

        MvcResult result = mockMvc.perform(delete("/medias/1")).andReturn();
        assertEquals(204, result.getResponse().getStatus());
    }

    /**
     * Verifies that attempting to delete a non-existent media resource results in 404 Not Found.
     *
     * <p>{@link ApiExceptionHandler}, loaded via {@code @ContextConfiguration},
     * intercepts the thrown {@link NotFoundException} and maps it to a 404 response.
     */
    @Test
    void delete_whenMediaNotFound_thenReturn404() throws Exception {
        doThrow(new NotFoundException("Media 99 is not found"))
            .when(mediaService).removeMedia(99L);

        MvcResult result = mockMvc.perform(delete("/medias/99")).andReturn();
        assertEquals(404, result.getResponse().getStatus());
    }

    // ==================== GET /medias/{id} ====================

    /**
     * Verifies that retrieving an existing media by ID returns 200 OK with full media details.
     */
    @Test
    void get_whenMediaExists_thenReturnMediaVm() throws Exception {
        MediaVm mediaVm = new MediaVm(
            1L, "caption", "photo.png", "image/png",
            "http://example.com/medias/1/file/photo.png"
        );
        when(mediaService.getMediaById(1L)).thenReturn(mediaVm);

        MvcResult result = mockMvc.perform(get("/medias/1")).andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertAll(
            () -> assertEquals(200, result.getResponse().getStatus()),
            () -> assertEquals(1L, body.get("id").asLong()),
            () -> assertEquals("caption", body.get("caption").asText()),
            () -> assertEquals("photo.png", body.get("fileName").asText()),
            () -> assertEquals("http://example.com/medias/1/file/photo.png", body.get("url").asText())
        );
    }

    /**
     * Verifies that retrieving a non-existent media by ID returns 404 Not Found.
     *
     * <p>The service returns {@code null} when no media is found, and the controller
     * maps this to a 404 response.
     */
    @Test
    void get_whenMediaNotFound_thenReturn404() throws Exception {
        when(mediaService.getMediaById(99L)).thenReturn(null);

        MvcResult result = mockMvc.perform(get("/medias/99")).andReturn();
        assertEquals(404, result.getResponse().getStatus());
    }

    // ==================== GET /medias?ids=... ====================

    /**
     * Verifies that retrieving multiple media resources by a list of IDs
     * returns 200 OK with the complete list.
     */
    @Test
    void getByIds_whenMediasExist_thenReturnList() throws Exception {
        MediaVm vm1 = new MediaVm(1L, "cap1", "file1.png", "image/png", "http://url/1");
        MediaVm vm2 = new MediaVm(2L, "cap2", "file2.jpg", "image/jpeg", "http://url/2");
        when(mediaService.getMediaByIds(anyList())).thenReturn(List.of(vm1, vm2));

        MvcResult result = mockMvc.perform(get("/medias")
                .param("ids", "1", "2"))
            .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertAll(
            () -> assertEquals(200, result.getResponse().getStatus()),
            () -> assertEquals(2, body.size()),
            () -> assertEquals(1L, body.get(0).get("id").asLong()),
            () -> assertEquals(2L, body.get(1).get("id").asLong())
        );
    }

    /**
     * Verifies that requesting media by IDs that do not exist returns 404 Not Found.
     */
    @Test
    void getByIds_whenNoMediaFound_thenReturn404() throws Exception {
        when(mediaService.getMediaByIds(anyList())).thenReturn(List.of());

        MvcResult result = mockMvc.perform(get("/medias")
                .param("ids", "99", "100"))
            .andReturn();

        assertEquals(404, result.getResponse().getStatus());
    }

    // ==================== GET /medias/{id}/file/{fileName} ====================

    /**
     * Verifies that downloading a file returns 200 OK with the binary content
     * and the correct {@code Content-Type} header.
     */
    @Test
    void getFile_whenFileExists_thenReturnFileContent() throws Exception {
        byte[] fileContent = "fake-image-content".getBytes();
        MediaDto mediaDto = MediaDto.builder()
            .content(new ByteArrayInputStream(fileContent))
            .mediaType(org.springframework.http.MediaType.IMAGE_PNG)
            .build();
        when(mediaService.getFile(anyLong(), anyString())).thenReturn(mediaDto);

        MvcResult result = mockMvc.perform(get("/medias/1/file/photo.png")).andReturn();

        assertAll(
            () -> assertEquals(200, result.getResponse().getStatus()),
            () -> assertNotNull(result.getResponse().getContentType()),
            () -> assertEquals(MediaType.IMAGE_PNG_VALUE, result.getResponse().getContentType()),
            () -> assertArrayEquals(fileContent, result.getResponse().getContentAsByteArray())
        );
    }

    /**
     * Generates the byte content of a minimal valid PNG image (1x1 pixel, RGB).
     *
     * <p>This helper uses {@link ImageIO#write} rather than hardcoded bytes because
     * {@code FileTypeValidator} calls {@link ImageIO#read} on the uploaded file's input stream
     * and checks that the result is non-null. If the PNG bytes have an incorrect CRC or
     * an incomplete structure, {@code ImageIO.read()} returns {@code null}, causing the
     * constraint to fail and the endpoint to return 400. Using {@code ImageIO.write()}
     * guarantees a structurally valid PNG output.
     */
    private byte[] createMinimalPngBytes() throws Exception {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, 0xFF0000);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return baos.toByteArray();
    }
}
