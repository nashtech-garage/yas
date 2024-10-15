package com.yas.commonlibrary.csv;

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


public class CsvExporter {

    public static <T> byte[] exportToCsv(List<T> dataList, Class<T> clazz) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8);

        // Write CSV header
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            CsvColumn columnAnnotation = field.getAnnotation(CsvColumn.class);
            if (columnAnnotation != null) {
                writer.append(columnAnnotation.columnName()).append(",");
            }
        }
        writer.append("\n");

        // Write CSV data
        for (T data : dataList) {
            for (Field field : fields) {
                CsvColumn columnAnnotation = field.getAnnotation(CsvColumn.class);
                if (columnAnnotation != null) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(data);
                        if (value instanceof List) {
                            writer.append(String.join(",", (List<String>) value));
                        } else {
                            writer.append(String.valueOf(value));
                        }
                        writer.append(",");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            writer.append("\n");
        }

        writer.flush();
        writer.close();

        return byteArrayOutputStream.toByteArray();
    }

    public static <T> String createFileName(Class<T> clazz) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        var fromDate = LocalDateTime.now().format(dateFormatter);
        CsvName csvName = clazz.getAnnotation(CsvName.class);
        return csvName.fileName() + "_" + fromDate + ".csv";
    }
}
