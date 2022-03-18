package com.epolsoft.reportwriterstarter.writer.implemenation;

import com.epolsoft.reportwriterstarter.writer.ReportWriter;
import com.epolsoft.reportwriterstarter.writer.ReportWriterType;
import com.epolsoft.reportwriterstarter.writer.TranslatableDTORegistry;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CsvFileWriter implements ReportWriter {

    private final TranslatableDTORegistry registry;

    public CsvFileWriter(TranslatableDTORegistry registry) {
        this.registry = registry;
    }

    @Override
    public ReportWriterType getType() {
        return ReportWriterType.CSV;
    }

    @Override
    public void writeToDisk(String filename, List<?> data) throws IOException, InvocationTargetException, IllegalAccessException {
        boolean append = Files.exists(Path.of(filename));
        ByteArrayOutputStream dataStream = writeToByteArrayOS(data, append);
        try (OutputStream outputStream = new FileOutputStream(filename, append)) {
            dataStream.writeTo(outputStream);
        }
    }

    @Override
    public ByteArrayOutputStream writeToByteArrayOS(List<?> data, boolean noHeader) throws IOException, InvocationTargetException, IllegalAccessException {
        CSVFormat csvFormat = CSVFormat.DEFAULT;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Class<?> dtoType = data.get(0).getClass();
        Map<String, Method> translatedNameMethodMap = registry.getTranslationsForDto(dtoType);
        if (translatedNameMethodMap == null) {
            registry.registerTranslations(dtoType);
            translatedNameMethodMap = registry.getTranslationsForDto(dtoType);
        }

        try (OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
             CSVPrinter csvPrinter = new CSVPrinter(streamWriter, csvFormat)) {
            if (!noHeader) {
                csvPrinter.printRecord(translatedNameMethodMap.keySet());
            }
            for (Object entry : data) {
                List<String> dataRecord = new ArrayList<>();
                for (Map.Entry<String, Method> mapEntry : translatedNameMethodMap.entrySet()) {
                    dataRecord.add(String.valueOf(mapEntry.getValue().invoke(entry)));
                }
                csvPrinter.printRecord(dataRecord);
            }
        }
        return outputStream;
    }
}
