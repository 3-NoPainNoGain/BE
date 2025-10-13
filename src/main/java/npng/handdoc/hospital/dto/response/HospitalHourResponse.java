package npng.handdoc.hospital.dto.response;

import npng.handdoc.hospital.domain.HospitalHour;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public record HospitalHourResponse(
        String day,
        String openTime,
        String closeTime
) {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static HospitalHourResponse from(HospitalHour hospitalHour){
        return new HospitalHourResponse(
                hospitalHour.getDay().name(),
                hospitalHour.getOpenTime().format(TIME_FORMATTER),
                hospitalHour.getCloseTime().format(TIME_FORMATTER)
        );
    }
}
