package com.yas.commonlibrary.csv;

import com.opencsv.CSVWriter;
import com.yas.commonlibrary.csv.anotation.CsvColumn;
import com.yas.commonlibrary.csv.anotation.CsvName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


@Slf4j
public class CsvExporter {

    public static final String COMMA = ",";

    public static <T> byte[] exportToCsv(List<BaseCsv> dataList, Class<T> clazz) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream,
                 StandardCharsets.UTF_8);
             CSVWriter csvWriter = new CSVWriter(outputStreamWriter, CSVWriter.DEFAULT_SEPARATOR,
                 CSVWriter.NO_QUOTE_CHARACTER,
                 CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {

            // Write CSV header
            writeCsvHeader(csvWriter, clazz);

            // Write CSV data
            writeCsvData(csvWriter, dataList, clazz);

            csvWriter.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    private static <T> void writeCsvHeader(CSVWriter csvWriter, Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        String[] header = Stream.of(fields)
            .filter(field -> field.getAnnotation(CsvColumn.class) != null)
            .map(field -> field.getAnnotation(CsvColumn.class).columnName())
            .toArray(String[]::new);
        ;
        csvWriter.writeNext(header);
    }

    private static <T> void writeCsvData(CSVWriter csvWriter, List<BaseCsv> dataList, Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (BaseCsv data : dataList) {
            String[] row = getFieldValues(fields, data);
            csvWriter.writeNext(row);
        }
    }


    private static String[] getFieldValues(Field[] fields, Object data) {
        return Stream.of(fields)
            .filter(field -> field.getAnnotation(CsvColumn.class) != null)
            .map(field -> getFieldValueAsString(field, data))
            .toArray(String[]::new);
    }

    private static String getFieldValueAsString(Field field, Object data) {
        try {
            field.setAccessible(true);
            Object value = field.get(data);
            if (!Objects.isNull(value) && value instanceof List) {
                return ("[" + String.join("|", (List<String>) value) + "]");
            }

            return value != null ? value.toString() : StringUtils.EMPTY;
        } catch (IllegalAccessException e) {
            log.warn("Get value field err ");
            e.printStackTrace();
            return StringUtils.EMPTY;
        }
    }

    public static <T> String createFileName(Class<T> clazz) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
        String fromDate = LocalDateTime.now().format(dateFormatter);
        CsvName csvName = clazz.getAnnotation(CsvName.class);
        return csvName.fileName() + "_" + fromDate + ".csv";
    }
}
