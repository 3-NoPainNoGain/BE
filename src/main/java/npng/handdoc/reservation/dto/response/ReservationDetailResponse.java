package npng.handdoc.reservation.dto.response;

import npng.handdoc.reservation.domain.Reservation;
import npng.handdoc.reservation.domain.type.Symptom;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationDetailResponse(
        String name,
        String residentId,
        String symptom,
        Long symptomDuration,
        String description,
        String doctorName,
        String hospitalName,
        String speciality,
        LocalDate slotDate,
        LocalTime startTime,
        LocalTime endTime) {
    public static ReservationDetailResponse from(Reservation reservation) {
        return new ReservationDetailResponse(
                reservation.getUser().getName(),
                reservation.getUser().getResidentId(),
                reservation.getSymptom().getLabel(),
                reservation.getSymptomDuration(),
                reservation.getDescription(),
                reservation.getDoctorProfile().getUser().getName(),
                reservation.getDoctorProfile().getHospitalName(),
                reservation.getDoctorProfile().getSpeciality().getLabel(),
                reservation.getSlotDate(),
                reservation.getStartTime(),
                reservation.getEndTime()
        );
    }
}
