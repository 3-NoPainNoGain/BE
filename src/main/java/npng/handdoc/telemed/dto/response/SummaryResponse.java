package npng.handdoc.telemed.dto.response;

import npng.handdoc.telemed.domain.Summary;
import npng.handdoc.user.domain.type.Speciality;

import java.time.LocalDate;

public record SummaryResponse(
        String speciality,
        String hospitalName,
        LocalDate slotDate,
        String consultationTime,
        String symptom,
        String impression,
        String prescription
) {
    public static SummaryResponse from(Summary summary) {
        return new SummaryResponse(
                summary.getTelemed().getReservation().getDoctorProfile().getSpeciality().getLabel(),
                summary.getTelemed().getReservation().getDoctorProfile().getHospitalName(),
                summary.getTelemed().getReservation().getSlotDate(),
                summary.getConsultationTime(),
                summary.getSymptom(),
                summary.getImpression(),
                summary.getPrescription()
        );
    }
}
