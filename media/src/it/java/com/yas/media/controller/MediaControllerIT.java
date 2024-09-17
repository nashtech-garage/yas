package com.yas.media.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.media.config.IntegrationTestConfiguration;
import com.yas.media.config.FilesystemConfig;
import com.yas.media.config.YasConfig;
import com.yas.media.model.Media;
import com.yas.media.repository.FileSystemRepository;
import com.yas.media.service.MediaService;
import com.yas.media.repository.MediaRepository;
import com.yas.media.viewmodel.MediaPostVm;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MediaControllerIT extends AbstractControllerIT {

    @Autowired
    private MediaRepository mediaRepository;
    @MockBean
    private YasConfig yasConfig;
    @MockBean
    private FileSystemRepository fileSystemRepository;
    @MockBean
    private FilesystemConfig filesystemConfig;
    @Autowired
    private MediaService mediaService;
    private Media media;

    private static final String MEDIA_URL = "/v1/medias";

    @BeforeEach
    public void insertTestData() {
        media = new Media();
        media.setId(1L);
        media.setCaption("test");
        media.setFileName("file");
        media.setMediaType("image/jpeg");

        media = mediaRepository.save(media);

        when(yasConfig.publicUrl()).thenReturn("/media/");
    }

    private MultipartFile createMultipart(String typeImage) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        bufferedImage.setRGB(0, 0, Color.RED.getRGB());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, typeImage, baos);
        baos.flush();

        byte[] fileContent = baos.toByteArray();

        return new MockMultipartFile(
            "file",
            "example." + typeImage,
            "image/" + typeImage,
            fileContent
        );
    }

    public static InputStream createFakeInputStream(String content) {
        return new ByteArrayInputStream(content.getBytes());
    }

    @AfterEach
    public void clearTestData() {
        mediaRepository.deleteAll();
    }

    @Test
    void test_getMedia_shouldReturnData_ifProvideValidId() {
        Long mediaId = media.getId();
        String mediaCaption = media.getCaption();
        given(getRequestSpecification())
            .when()
            .get(MEDIA_URL + '/' + mediaId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("caption", equalTo(mediaCaption))
            .log().ifValidationFails();
    }

    @Test
    void test_getMedia_shouldThrows404_ifProvideInvalidId() {
        given(getRequestSpecification())
            .when()
            .get(MEDIA_URL + '/' + 1000)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteMedia_shouldDelete_ifProvideValidAccessTokenAndValidId() {
        Long mediaId = media.getId();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .when()
            .delete(MEDIA_URL + '/' + mediaId)
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteMedia_shouldReturn404_ifProvideValidAccessTokenAndInvalidId() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .when()
            .delete(MEDIA_URL + '/' + 1000)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteMedia_shouldThrows403_ifProvideInvalidAccessToken() {
        Long mediaId = media.getId();
        given(getRequestSpecification())
            .when()
            .delete(MEDIA_URL + '/' + mediaId)
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .log().ifValidationFails();
    }

    @Test
    void test_createMedia_shouldReturn403_ifProvideInvalidAccessToken() throws IOException {
        MultipartFile multipartFile = createMultipart("png");
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, "fileName");
        given(getRequestSpecification())
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .multiPart("multipartFile", mediaPostVm.multipartFile().getName(), mediaPostVm.multipartFile().getBytes(), mediaPostVm.multipartFile().getContentType())
            .formParam("caption", mediaPostVm.caption())
            .formParam("fileNameOverride", mediaPostVm.fileNameOverride())
            .when()
            .post(MEDIA_URL)
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .log().ifValidationFails();
    }

    @Test
    void test_createMediaPNG_shouldSuccess_ifProvideValidAccessTokenAndValidData() throws IOException {
        MultipartFile multipartFile = createMultipart("png");
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, "fileName");
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .multiPart("multipartFile", mediaPostVm.multipartFile().getName(), mediaPostVm.multipartFile().getBytes(), mediaPostVm.multipartFile().getContentType())
            .formParam("caption", mediaPostVm.caption())
            .formParam("fileNameOverride", mediaPostVm.fileNameOverride())
            .when()
            .post("/v1/medias")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("caption", equalTo(mediaPostVm.caption()))
            .body("mediaType", equalTo(mediaPostVm.multipartFile().getContentType()))
            .body("fileName", equalTo(mediaPostVm.fileNameOverride()))
            .log().ifValidationFails();
    }

    @Test
    void test_createMediaJPEG_shouldSuccess_ifProvideValidAccessTokenAndValidData() throws IOException {
        MultipartFile multipartFile = createMultipart("jpeg");
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, "fileName");
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .multiPart("multipartFile", mediaPostVm.multipartFile().getName(), mediaPostVm.multipartFile().getBytes(), mediaPostVm.multipartFile().getContentType())
            .formParam("caption", mediaPostVm.caption())
            .formParam("fileNameOverride", mediaPostVm.fileNameOverride())
            .when()
            .post(MEDIA_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("caption", equalTo(mediaPostVm.caption()))
            .body("mediaType", equalTo(mediaPostVm.multipartFile().getContentType()))
            .body("fileName", equalTo(mediaPostVm.fileNameOverride()))
            .log().ifValidationFails();
    }

    @Test
    void test_createMediaGIF_shouldSuccess_ifProvideValidAccessTokenAndValidData() throws IOException {
        MultipartFile multipartFile = createMultipart("gif");
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, "fileName");
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .multiPart("multipartFile", mediaPostVm.multipartFile().getName(), mediaPostVm.multipartFile().getBytes(), mediaPostVm.multipartFile().getContentType())
            .formParam("caption", mediaPostVm.caption())
            .formParam("fileNameOverride", mediaPostVm.fileNameOverride())
            .when()
            .post(MEDIA_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("caption", equalTo(mediaPostVm.caption()))
            .body("mediaType", equalTo(mediaPostVm.multipartFile().getContentType()))
            .body("fileName", equalTo(mediaPostVm.fileNameOverride()))
            .log().ifValidationFails();
    }

    @Test
    void test_createWrongTypeMedia_shouldFail_ifProvideValidAccessTokenAndInvalidData() throws IOException {
        MultipartFile multipartFile = createMultipart("txt");
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, "fileName");
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .multiPart("multipartFile", mediaPostVm.multipartFile().getName(), mediaPostVm.multipartFile().getBytes(), mediaPostVm.multipartFile().getContentType())
            .formParam("caption", mediaPostVm.caption())
            .formParam("fileNameOverride", mediaPostVm.fileNameOverride())
            .when()
            .post(MEDIA_URL)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .log().ifValidationFails();
    }

    @Test
    void test_createMediaFileNameNull_shouldSuccess_ifProvideValidAccessTokenAndValidData() throws IOException {
        MultipartFile multipartFile = createMultipart("png");
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, null);
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .multiPart("multipartFile", mediaPostVm.multipartFile().getOriginalFilename(), mediaPostVm.multipartFile().getBytes(), mediaPostVm.multipartFile().getContentType())
            .formParam("caption", mediaPostVm.caption())
            .formParam("fileNameOverride", mediaPostVm.fileNameOverride())
            .when()
            .post(MEDIA_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("caption", equalTo(mediaPostVm.caption()))
            .body("mediaType", equalTo(mediaPostVm.multipartFile().getContentType()))
            .body("fileName", equalTo(multipartFile.getOriginalFilename()))
            .log().ifValidationFails();
    }

    @Test
    void test_createMediaFileNameEmpty_shouldSuccess_ifProvideValidAccessTokenAndValidData() throws IOException {
        MultipartFile multipartFile = createMultipart("png");
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, "");
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .multiPart("multipartFile", mediaPostVm.multipartFile().getOriginalFilename(), mediaPostVm.multipartFile().getBytes(), mediaPostVm.multipartFile().getContentType())
            .formParam("caption", mediaPostVm.caption())
            .formParam("fileNameOverride", "")
            .when()
            .post(MEDIA_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("caption", equalTo(mediaPostVm.caption()))
            .body("mediaType", equalTo(mediaPostVm.multipartFile().getContentType()))
            .body("fileName", equalTo(multipartFile.getOriginalFilename()))
            .log().ifValidationFails();
    }

    @Test
    void test_createMediaFileNameBlank_shouldSuccess_ifProvideValidAccessTokenAndValidData() throws IOException {
        MultipartFile multipartFile = createMultipart("png");
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, "  ");
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .multiPart("multipartFile", mediaPostVm.multipartFile().getOriginalFilename(), mediaPostVm.multipartFile().getBytes(), mediaPostVm.multipartFile().getContentType())
            .formParam("caption", mediaPostVm.caption())
            .formParam("fileNameOverride", "  ")
            .when()
            .post(MEDIA_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("caption", equalTo(mediaPostVm.caption()))
            .body("mediaType", equalTo(mediaPostVm.multipartFile().getContentType()))
            .body("fileName", equalTo(multipartFile.getOriginalFilename()))
            .log().ifValidationFails();
    }

    @Test
    void test_getFile_shouldSuccess_ifProvideValidIdAndValidFileName() throws IOException {
        Long mediaId = media.getId();
        String fileName = media.getFileName();
        when(fileSystemRepository.getFile(any())).thenReturn(createFakeInputStream("test"));

        System.out.println(fileSystemRepository.getFile("test") == null);
        given(getRequestSpecification())
            .pathParam("id", mediaId)
            .pathParam("fileName", fileName)
            .when()
            .get("/v1/medias/{id}/file/{fileName}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .log().ifValidationFails();
    }
}
