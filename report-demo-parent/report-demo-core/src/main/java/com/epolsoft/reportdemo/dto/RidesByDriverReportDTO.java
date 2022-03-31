package com.epolsoft.reportdemo.dto;

import com.epolsoft.reportwriterstarter.annotation.FieldNameTranslation;
import com.epolsoft.reportwriterstarter.annotation.TranslatableDTO;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;
import java.time.LocalDateTime;

@Builder
@Getter
@TranslatableDTO(ignoreNonAnnotated = false)
public class RidesByDriverReportDTO {

    @FieldNameTranslation(value = "Imię i nazwisko kierowcy", order = 1)
    private String driverName;

    @FieldNameTranslation(value = "Pojazd", order = 2)
    private String car;

    @FieldNameTranslation(value = "Skąd-dokąd", order = 3)
    private String route;

    @FieldNameTranslation(value = "Pojazd przypisany", order = 4)
    private LocalDateTime timeCarAssigned;

    @FieldNameTranslation(value = "Pojazd przyjechał", order = 5)
    private LocalDateTime timeCarArrived;

    @FieldNameTranslation(ignore = true)
    private LocalDateTime timeRideStarted;
    @FieldNameTranslation(ignore = true)
    private LocalDateTime timeRideFinished;

    private String clientPhone;

    @FieldNameTranslation(value = "Czas jazdy", order = 6)
    public String getRideDuration() {
        Duration rideDuration = Duration.between(timeRideStarted, timeRideFinished);
        return DurationFormatUtils.formatDuration(rideDuration.toMillis(), "HH:mm:ss", true);
    }
}
