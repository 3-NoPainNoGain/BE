package npng.handdoc.reservation.dto.response;

import npng.handdoc.reservation.domain.Reservation;
import npng.handdoc.reservation.domain.type.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationItemResponse(
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
    public static ReservationItemResponse from(Reservation reservation) {
        return new ReservationItemResponse(
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
