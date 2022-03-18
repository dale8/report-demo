package com.epolsoft.reportdemo.ifrastructure;

import com.epolsoft.reportwriterstarter.writer.ReportWriter;
import com.epolsoft.reportwriterstarter.writer.ReportWriterType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ReportWriterResolver {

    private final Map<ReportWriterType, ReportWriter> writerMap;

    public ReportWriterResolver(@Autowired List<ReportWriter> writerList) {
        writerMap = new EnumMap<>(ReportWriterType.class);
        writerList.forEach(reportWriter -> writerMap.put(reportWriter.getType(), reportWriter));
    }

    public ReportWriter resolve(ReportWriterType type) {
        return writerMap.get(type);
    }
}
