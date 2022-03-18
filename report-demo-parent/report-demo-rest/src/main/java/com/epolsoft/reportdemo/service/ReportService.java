package com.epolsoft.reportdemo.service;

import com.epolsoft.reportdemo.dto.RidesByDriverReportDTO;
import com.epolsoft.reportdemo.ifrastructure.ReportWriterResolver;
import com.epolsoft.reportdemo.repository.RidesByDriverReportRepo;
import com.epolsoft.reportwriterstarter.writer.ReportWriter;
import com.epolsoft.reportwriterstarter.writer.ReportWriterType;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private RidesByDriverReportRepo reportRepo;
    @Autowired
    private ReportWriterResolver reportResolver;

    @SneakyThrows
    public ByteArrayResource getReportAsFile(Long driverId, ReportWriterType writerType) {
        List<RidesByDriverReportDTO> data = reportRepo.fetchReport(driverId);
        ReportWriter writer = reportResolver.resolve(writerType);
        ByteArrayOutputStream reportStream = writer.writeToByteArrayOS(data, false);
        return new ByteArrayResource(reportStream.toByteArray());
    }
}
