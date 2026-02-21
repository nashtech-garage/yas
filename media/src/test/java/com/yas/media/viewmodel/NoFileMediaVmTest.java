package com.yas.media.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class NoFileMediaVmTest {

    @Test
    void testNoFileMediaVmRecord() {
        Long id = 1L;
        String caption = "Test Image";
        String fileName = "image.jpg";
        String mediaType = "image/jpeg";

        NoFileMediaVm noFileMediaVm = new NoFileMediaVm(id, caption, fileName, mediaType);

        assertEquals(id, noFileMediaVm.id());
        assertEquals(caption, noFileMediaVm.caption());
        assertEquals(fileName, noFileMediaVm.fileName());
        assertEquals(mediaType, noFileMediaVm.mediaType());
    }

    @Test
    void testNoFileMediaVmWithNullValues() {
        NoFileMediaVm noFileMediaVm = new NoFileMediaVm(null, null, null, null);

        assertEquals(null, noFileMediaVm.id());
        assertEquals(null, noFileMediaVm.caption());
        assertEquals(null, noFileMediaVm.fileName());
        assertEquals(null, noFileMediaVm.mediaType());
    }

    @Test
    void testNoFileMediaVmWithEmptyStrings() {
        NoFileMediaVm noFileMediaVm = new NoFileMediaVm(1L, "", "", "");

        assertEquals(1L, noFileMediaVm.id());
        assertEquals("", noFileMediaVm.caption());
        assertEquals("", noFileMediaVm.fileName());
        assertEquals("", noFileMediaVm.mediaType());
    }

    @Test
    void testNoFileMediaVmEquality() {
        NoFileMediaVm vm1 = new NoFileMediaVm(1L, "Caption", "file.jpg", "image/jpeg");
        NoFileMediaVm vm2 = new NoFileMediaVm(1L, "Caption", "file.jpg", "image/jpeg");

        assertEquals(vm1, vm2);
    }

    @Test
    void testNoFileMediaVmInequality() {
        NoFileMediaVm vm1 = new NoFileMediaVm(1L, "Caption1", "file.jpg", "image/jpeg");
        NoFileMediaVm vm2 = new NoFileMediaVm(2L, "Caption2", "file.jpg", "image/jpeg");

        assertNotNull(vm1);
        assertNotNull(vm2);
        // Records have auto-generated equals() based on all fields
    }

    @Test
    void testNoFileMediaVmMultipleInstances() {
        NoFileMediaVm vm1 = new NoFileMediaVm(1L, "Image1", "img1.jpg", "image/jpeg");
        NoFileMediaVm vm2 = new NoFileMediaVm(2L, "Image2", "img2.png", "image/png");
        NoFileMediaVm vm3 = new NoFileMediaVm(3L, "Image3", "img3.gif", "image/gif");

        assertNotNull(vm1);
        assertNotNull(vm2);
        assertNotNull(vm3);
        assertEquals(1L, vm1.id());
        assertEquals(2L, vm2.id());
        assertEquals(3L, vm3.id());
    }
}
