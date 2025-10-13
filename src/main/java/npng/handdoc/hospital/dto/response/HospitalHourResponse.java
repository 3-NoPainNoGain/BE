package npng.handdoc.hospital.dto.response;

import npng.handdoc.hospital.domain.HospitalHour;

import java.time.LocalTime;

public record HospitalHourResponse(
        String day,
        LocalTime openTime,
        LocalTime closeTime
) {
    public static HospitalHourResponse from(HospitalHour hospitalHour){
        return new HospitalHourResponse(
                hospitalHour.getDay().name(),
                hospitalHour.getOpenTime(),
                hospitalHour.getCloseTime()
        );
    }
}
