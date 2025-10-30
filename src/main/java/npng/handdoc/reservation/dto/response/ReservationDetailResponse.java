package npng.handdoc.reservation.dto.response;

import npng.handdoc.reservation.domain.Reservation;
import npng.handdoc.reservation.domain.type.Option;
import npng.handdoc.reservation.domain.type.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;


public record ReservationDetailResponse(
        Long reservationId,
        String name,
        String residentId,
        String symptom,
        Long symptomDuration,
        Option interpretationOption,
        ReservationStatus status,
        String description,
        String doctorName,
        String hospitalName,
        String speciality,
        LocalDate slotDate,
        LocalTime startTime,
        LocalTime endTime) {
    public static ReservationDetailResponse from(Reservation reservation) {
        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getUser().getName(),
                reservation.getUser().getResidentId(),
                reservation.getSymptom().getLabel(),
                reservation.getSymptomDuration(),
                reservation.getInterpretationOption(),
                reservation.getStatus(),
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
