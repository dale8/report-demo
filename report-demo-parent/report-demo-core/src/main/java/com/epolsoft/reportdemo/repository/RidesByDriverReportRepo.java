package com.epolsoft.reportdemo.repository;

import com.epolsoft.reportdemo.dto.RidesByDriverReportDTO;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.epolsoft.reportdemo.generatedmodel.tables.Car.CAR;
import static com.epolsoft.reportdemo.generatedmodel.tables.Client.CLIENT;
import static com.epolsoft.reportdemo.generatedmodel.tables.Driver.DRIVER;
import static com.epolsoft.reportdemo.generatedmodel.tables.Order.ORDER;

@Repository
public class RidesByDriverReportRepo {
    private final DSLContext jooqDsl;

    @Autowired
    public RidesByDriverReportRepo(DSLContext jooqDsl) {
        this.jooqDsl = jooqDsl;
    }

    public List<RidesByDriverReportDTO> fetchReport(Long driverId) {
        SelectConditionStep<Record> records = jooqDsl.select(
                        ORDER.ROUTE_START,
                        ORDER.ROUTE_FINISH,
                        ORDER.TIME_CAR_ASSIGNED,
                        ORDER.TIME_CAR_ARRIVED,
                        ORDER.TIME_RIDE_STARTED,
                        ORDER.TIME_RIDE_FINISHED
                ).select(
                        DRIVER.NAME
                ).select(
                        CAR.MAKE,
                        CAR.MODEL
                ).select(
                        CLIENT.PHONE
                )
                .from(ORDER)
                .leftJoin(DRIVER).on(ORDER.DRIVER.eq(DRIVER.ID))
                .leftJoin(CAR).on(DRIVER.CAR.eq(CAR.ID))
                .leftJoin(CLIENT).on(ORDER.CLIENT.eq(CLIENT.ID))
                .where(ORDER.DRIVER.eq(driverId));

        return records.stream()
                .map(this::mapRecordToDTO)
                .sorted(Comparator.comparing(RidesByDriverReportDTO::getTimeCarArrived))
                .collect(Collectors.toList());

    }

    private RidesByDriverReportDTO mapRecordToDTO(Record jooqRecord) {
        return RidesByDriverReportDTO.builder()
                .driverName(jooqRecord.get(DRIVER.NAME))
                .car(getCar(jooqRecord))
                .route(getRoute(jooqRecord))
                .timeCarAssigned(jooqRecord.get(ORDER.TIME_CAR_ASSIGNED))
                .timeCarArrived(jooqRecord.get(ORDER.TIME_CAR_ARRIVED))
                .timeRideStarted(jooqRecord.get(ORDER.TIME_RIDE_STARTED))
                .timeRideFinished(jooqRecord.get(ORDER.TIME_RIDE_FINISHED))
                .clientPhone(jooqRecord.get(CLIENT.PHONE))
                .build();
    }

    private String getRoute(Record jooqRecord) {
        return jooqRecord.get(ORDER.ROUTE_START) +
                " - " +
                jooqRecord.get(ORDER.ROUTE_FINISH);
    }

    private String getCar(Record jooqRecord) {
        return jooqRecord.get(CAR.MAKE) +
                " " +
                jooqRecord.get(CAR.MODEL);
    }
}
