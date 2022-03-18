package com.epolsoft.reportwriterstarter;

import com.epolsoft.reportwriterstarter.infrastructure.TranslatableDTORegistrarBeanPostProcessor;
import com.epolsoft.reportwriterstarter.writer.*;
import com.epolsoft.reportwriterstarter.writer.implemenation.CsvFileWriter;
import com.epolsoft.reportwriterstarter.writer.implemenation.ExcelFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportWriterConfig {

    @Bean
    public TranslatableDTORegistry translatableDTORegistry() {
        return new TranslatableDTORegistry();
    }

    @Bean
    public TranslatableDTORegistrarBeanPostProcessor translatableDTORegistrarBeanPostProcessor(TranslatableDTORegistry registry) {
        return new TranslatableDTORegistrarBeanPostProcessor(registry);
    }

    @Bean
    public CsvFileWriter csvReportWriter(TranslatableDTORegistry registry) {
        return new CsvFileWriter(registry);
    }

    @Bean
    public ExcelFileWriter excelReportWriter(TranslatableDTORegistry registry) {
        return new ExcelFileWriter(registry);
    }
}
