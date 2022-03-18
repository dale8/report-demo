package com.epolsoft.reportwriterstarter.writer.implemenation;

import com.epolsoft.reportwriterstarter.writer.ReportWriter;
import com.epolsoft.reportwriterstarter.writer.ReportWriterType;
import com.epolsoft.reportwriterstarter.writer.TranslatableDTORegistry;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExcelFileWriter implements ReportWriter {

    private final TranslatableDTORegistry registry;

    public ExcelFileWriter(TranslatableDTORegistry registry) {
        this.registry = registry;
    }

    @Override
    public ReportWriterType getType() {
        return ReportWriterType.EXCEL;
    }

    @Override
    public void writeToDisk(String filename, List<?> data) throws IOException, InvocationTargetException, IllegalAccessException {
        boolean append = Files.exists(Path.of(filename));
        ByteArrayOutputStream dataStream;
        if (append) {
            try (FileInputStream inputStream = new FileInputStream(filename)) {
                HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
                dataStream = writeToByteArrayOS(data, true, workbook);
            }
        } else {
            dataStream = writeToByteArrayOS(data, false);
        }

        try (OutputStream outputStream = new FileOutputStream(filename)) {
            dataStream.writeTo(outputStream);
        }
    }

    @Override
    public ByteArrayOutputStream writeToByteArrayOS(List<?> data, boolean noHeader) throws IOException, IllegalAccessException, InvocationTargetException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        return writeToByteArrayOS(data, noHeader, workbook);
    }

    public ByteArrayOutputStream writeToByteArrayOS(List<?> data, boolean noHeader, HSSFWorkbook workbook) throws IOException, InvocationTargetException, IllegalAccessException {
        Class<?> dtoType = data.get(0).getClass();
        Map<String, Method> translatedNameMethodMap = registry.getTranslationsForDto(dtoType);
        if (translatedNameMethodMap == null) {
            registry.registerTranslations(dtoType);
            translatedNameMethodMap = registry.getTranslationsForDto(dtoType);
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            HSSFSheet sheet;
            int rowNum = 0;
            int cellCounter = 0;
            if (noHeader) {
                sheet = workbook.getSheetAt(0);
                rowNum = sheet.getLastRowNum();
            } else {
                sheet = workbook.createSheet();
            }

            HSSFRow row;
            if (!noHeader) {
                row = sheet.createRow(rowNum);
                for (String key : translatedNameMethodMap.keySet()) {
                    HSSFCell cell = row.createCell(cellCounter, CellType.STRING);
                    cell.setCellValue(key);
                    cellCounter++;
                }
            }

            for (Object entry : data) {
                cellCounter = 0;
                rowNum++;
                row = sheet.createRow(rowNum);
                for (Map.Entry<String, Method> mapEntry : translatedNameMethodMap.entrySet()) {
                    HSSFCell cell = row.createCell(cellCounter, CellType.STRING);
                    String cellValue = String.valueOf(mapEntry.getValue().invoke(entry));
                    cell.setCellValue(Objects.requireNonNullElse(cellValue, ""));
                    cellCounter++;
                }
            }

            workbook.write(outputStream);
            workbook.close();
            return outputStream;
        }
    }
}
