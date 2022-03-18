package com.epolsoft.reportdemo.controller;

import com.epolsoft.reportdemo.service.ReportService;
import com.epolsoft.reportwriterstarter.writer.ReportWriterType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report/rides-by-driver")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadReport(@RequestParam Long driverId, @RequestParam ReportWriterType writerType) {
        if (writerType == null) {
            writerType = ReportWriterType.CSV;
        }
        ByteArrayResource resource = reportService.getReportAsFile(driverId, writerType);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report." + writerType.getExtension())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
