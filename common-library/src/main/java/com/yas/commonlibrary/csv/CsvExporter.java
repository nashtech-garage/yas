package com.yas.commonlibrary.csv;

import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import com.yas.commonlibrary.csv.anotation.CsvColumn;
import com.yas.commonlibrary.csv.anotation.CsvName;
import com.yas.commonlibrary.utils.DateTimeUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


@Slf4j
public class CsvExporter {

    private CsvExporter() {
    }

    private static final String GET_PREFIX = "get";

    public static <T> byte[] exportToCsv(List<BaseCsv> dataList, Class<T> clazz) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream,
                 StandardCharsets.UTF_8);
             CSVWriter csvWriter = new CSVWriter(outputStreamWriter, ICSVWriter.DEFAULT_SEPARATOR,
                 ICSVWriter.NO_QUOTE_CHARACTER,
                 ICSVWriter.DEFAULT_ESCAPE_CHARACTER, ICSVWriter.DEFAULT_LINE_END)) {

            // Write CSV header
            writeCsvHeader(csvWriter, clazz);

            // Write CSV data
            writeCsvData(csvWriter, dataList, clazz);

            csvWriter.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    private static <T> void writeCsvHeader(CSVWriter csvWriter, Class<T> clazz) {
        Field[] baseFields = BaseCsv.class.getDeclaredFields();
        Field[] fields = clazz.getDeclaredFields();

        String[] header = Stream.concat(Stream.of(baseFields), Stream.of(fields))
            .filter(field -> field.getAnnotation(CsvColumn.class) != null)
            .map(field -> field.getAnnotation(CsvColumn.class).columnName())
            .toArray(String[]::new);

        csvWriter.writeNext(header);
    }

    private static <T> void writeCsvData(CSVWriter csvWriter, List<BaseCsv> dataList,
                                         Class<T> clazz) {
        Field[] baseFields = BaseCsv.class.getDeclaredFields();
        Field[] fields = clazz.getDeclaredFields();

        Field[] allFields = Stream.concat(Stream.of(baseFields), Stream.of(fields))
            .filter(field -> field.getAnnotation(CsvColumn.class) != null)
            .toArray(Field[]::new);

        for (BaseCsv data : dataList) {
            String[] row = getFieldValues(allFields, data);
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
            String getterName = GET_PREFIX + StringUtils.capitalize(field.getName());
            Method getter = data.getClass().getMethod(getterName);
            Object value = getter.invoke(data);

            if (!Objects.isNull(value) && value instanceof List) {
                return ("[" + String.join("|", (List<String>) value) + "]");
            }

            return value != null ? value.toString() : StringUtils.EMPTY;
        } catch (IllegalAccessException e) {
            log.warn("Get value field err {}", e.getMessage());
            return StringUtils.EMPTY;
        } catch (InvocationTargetException | NoSuchMethodException e) {
            log.warn("Get value err {}", e.getMessage());
            return StringUtils.EMPTY;
        }
    }

    public static <T> String createFileName(Class<T> clazz) {
        String fromDate = DateTimeUtils.format(LocalDateTime.now());
        CsvName csvName = clazz.getAnnotation(CsvName.class);
        return String.format("%s_%s.csv", csvName.fileName(), fromDate);
    }
}
