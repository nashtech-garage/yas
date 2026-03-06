package com.yas.media.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class MediaPostVmTest {

    @Test
    void testMediaPostVmRecord() {
        String caption = "Test Caption";
        MultipartFile multipartFile = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test content".getBytes()
        );
        String fileNameOverride = "custom-name.jpg";

        MediaPostVm mediaPostVm = new MediaPostVm(caption, multipartFile, fileNameOverride);

        assertEquals(caption, mediaPostVm.caption());
        assertEquals(multipartFile, mediaPostVm.multipartFile());
        assertEquals(fileNameOverride, mediaPostVm.fileNameOverride());
    }

    @Test
    void testMediaPostVmWithNullCaption() {
        MultipartFile multipartFile = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        MediaPostVm mediaPostVm = new MediaPostVm(null, multipartFile, "override.jpg");

        assertNull(mediaPostVm.caption());
        assertNotNull(mediaPostVm.multipartFile());
        assertEquals("override.jpg", mediaPostVm.fileNameOverride());
    }

    @Test
    void testMediaPostVmWithNullFileNameOverride() {
        MultipartFile multipartFile = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        MediaPostVm mediaPostVm = new MediaPostVm("Caption", multipartFile, null);

        assertEquals("Caption", mediaPostVm.caption());
        assertNotNull(mediaPostVm.multipartFile());
        assertNull(mediaPostVm.fileNameOverride());
    }

    @Test
    void testMediaPostVmWithEmptyCaption() {
        MultipartFile multipartFile = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        MediaPostVm mediaPostVm = new MediaPostVm("", multipartFile, "override.jpg");

        assertEquals("", mediaPostVm.caption());
        assertNotNull(mediaPostVm.multipartFile());
    }

    @Test
    void testMediaPostVmMultipleFiles() {
        MultipartFile file1 = new MockMultipartFile(
            "file",
            "image1.jpg",
            "image/jpeg",
            "content1".getBytes()
        );
        MultipartFile file2 = new MockMultipartFile(
            "file",
            "image2.png",
            "image/png",
            "content2".getBytes()
        );

        MediaPostVm vm1 = new MediaPostVm("Image1", file1, "custom1.jpg");
        MediaPostVm vm2 = new MediaPostVm("Image2", file2, "custom2.png");

        assertNotNull(vm1);
        assertNotNull(vm2);
        assertEquals("Image1", vm1.caption());
        assertEquals("Image2", vm2.caption());
    }

    @Test
    void testMediaPostVmFileContent() {
        byte[] fileContent = "test file content".getBytes();
        MultipartFile multipartFile = new MockMultipartFile(
            "file",
            "document.jpg",
            "image/jpeg",
            fileContent
        );

        MediaPostVm mediaPostVm = new MediaPostVm("Document", multipartFile, null);

        assertEquals("Document", mediaPostVm.caption());
        assertEquals("image/jpeg", mediaPostVm.multipartFile().getContentType());
        assertEquals("document.jpg", mediaPostVm.multipartFile().getOriginalFilename());
    }
}
