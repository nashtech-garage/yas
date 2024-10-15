package com.yas.commonlibrary.csv;

import com.yas.commonlibrary.csv.anotation.CsvColumn;
import com.yas.commonlibrary.csv.anotation.CsvName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvExporterTest {

    @CsvName(fileName = "TestFile")
    static class TestData {
        @CsvColumn(columnName = "ID")
        private int id;

        @CsvColumn(columnName = "Name")
        private String name;

        @CsvColumn(columnName = "Tags")
        private List<String> tags;

        public TestData(int id, String name, List<String> tags) {
            this.id = id;
            this.name = name;
            this.tags = tags;
        }
    }

    @Test
    void testExportToCsv_withValidData_shouldReturnCorrectCsvContent() throws IOException {
        // Given
        List<TestData> dataList = Arrays.asList(
                new TestData(1, "Alice", Arrays.asList("tag1", "tag2")),
                new TestData(2, "Bob", Arrays.asList("tag3", "tag4"))
        );

        // When
        byte[] csvBytes = CsvExporter.exportToCsv(dataList, TestData.class);
        String csvContent = new String(csvBytes);

        // Then
        String expectedCsv = "ID,Name,Tags,\n" +
                "1,Alice,tag1,tag2,\n" +
                "2,Bob,tag3,tag4,\n";

        assertEquals(expectedCsv, csvContent);
    }

    @Test
    void testExportToCsv_withEmptyDataList_shouldReturnOnlyHeader() throws IOException {
        // Given
        List<TestData> dataList = new ArrayList<>();

        // When
        byte[] csvBytes = CsvExporter.exportToCsv(dataList, TestData.class);
        String csvContent = new String(csvBytes);

        // Then
        String expectedCsv = "ID,Name,Tags,\n";
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
