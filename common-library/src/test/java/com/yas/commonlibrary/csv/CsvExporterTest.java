package com.yas.commonlibrary.csv;

import com.yas.commonlibrary.csv.anotation.CsvColumn;
import com.yas.commonlibrary.csv.anotation.CsvName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvExporterTest {

    @SuperBuilder
    @CsvName(fileName = "TestFile")
    @Getter
    @Setter
    static class TestData extends BaseCsv {

        @CsvColumn(columnName = "Name")
        private String name;

        @CsvColumn(columnName = "Tags")
        private List<String> tags;
    }

    @Test
    void testExportToCsv_withValidData_shouldReturnCorrectCsvContent() throws IOException {
        // Given
        List<BaseCsv> dataList = Arrays.asList(
            TestData.builder()
                .id(1L)
                .name("Alice")
                .tags(Arrays.asList("tag1", "tag2"))
                .build(),
            TestData.builder()
                .id(2L)
                .name("Bob")
                .tags(Arrays.asList("tag3", "tag4"))
                .build()
        );
        // When
        byte[] csvBytes = CsvExporter.exportToCsv(dataList, TestData.class);
        String csvContent = new String(csvBytes);

        // Then
        String expectedCsv = """
            Id,Name,Tags
            1,Alice,[tag1|tag2]
            2,Bob,[tag3|tag4]
            """;

        assertEquals(expectedCsv, csvContent);
    }

    @Test
    void testExportToCsv_withEmptyDataList_shouldReturnOnlyHeader() throws IOException {
        // Given
        List<BaseCsv> dataList = new ArrayList<>();

        // When
        byte[] csvBytes = CsvExporter.exportToCsv(dataList, TestData.class);
        String csvContent = new String(csvBytes);

        // Then
        String expectedCsv = "Id,Name,Tags\n";
        assertEquals(expectedCsv, csvContent);
    }

    @Test
    void testCreateFileName_withValidClass_shouldReturnCorrectFileName() {
        // Given
        Class<TestData> clazz = TestData.class;

        // When
        String fileName = CsvExporter.createFileName(clazz);

        // Then
        String expectedPrefix = "TestFile_";
        assertTrue(fileName.startsWith(expectedPrefix));
        assertTrue(fileName.endsWith(".csv"));
    }
}
