package npng.handdoc.reservation.dto.response;

import npng.handdoc.reservation.domain.Reservation;
import npng.handdoc.reservation.domain.type.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationResponse(
        Long id,
        Long patientId,
        Long doctorProfileId,
        ReservationStatus status,
        String symptom,
        String description,
        LocalDate slotDate,
        LocalTime startTime,
        LocalTime endTime
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getDoctorProfile().getId(),
                reservation.getStatus(),
                reservation.getSymptom().getLabel(),
                reservation.getDescription(),
                reservation.getSlotDate(),
                reservation.getStartTime(),
                reservation.getEndTime()
        );
    }
}
