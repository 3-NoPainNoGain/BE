package npng.handdoc.telemed.dto.response;

import npng.handdoc.reservation.domain.Reservation;
import npng.handdoc.telemed.domain.Telemed;

import java.time.LocalDate;
import java.time.LocalTime;

public record HistoryItemResponse(
        LocalDate slotDate,
        LocalTime startTime,
        LocalTime endTime,
        String doctorName,
        String hospitalName,
        String symptom
) {

    public static HistoryItemResponse from(Reservation reservation, Telemed telemed) {
        return new HistoryItemResponse(
                reservation.getSlotDate(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getDoctorProfile().getUser().getName(),
                reservation.getDoctorProfile().getHospitalName(),
                telemed.getSummary().getSymptom());
    }
}
