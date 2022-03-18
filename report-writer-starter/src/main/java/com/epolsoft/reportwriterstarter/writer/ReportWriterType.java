package com.epolsoft.reportwriterstarter.writer;

public enum ReportWriterType {
    CSV("csv"),
    EXCEL("xls");

    private final String extension;

    ReportWriterType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
