package com.epolsoft.reportwriterstarter.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface ReportWriter {
    ReportWriterType getType();

    void writeToDisk(String filename, List<?> data) throws IOException, InvocationTargetException, IllegalAccessException;

    ByteArrayOutputStream writeToByteArrayOS(List<?> data, boolean noHeader) throws IOException, InvocationTargetException, IllegalAccessException;
}
