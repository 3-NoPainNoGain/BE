package npng.handdoc.telemed.dto.response;

import npng.handdoc.telemed.domain.Summary;

import java.time.LocalDate;

public record SummaryResponse(
        String hospitalName,
        LocalDate slotDate,
        String consultationTime,
        String symptom,
        String impression,
        String prescription
) {
    public static SummaryResponse from(Summary summary) {
        return new SummaryResponse(
                summary.getTelemed().getReservation().getDoctorProfile().getHospitalName(),
                summary.getTelemed().getReservation().getSlotDate(),
                summary.getConsultationTime(),
                summary.getSymptom(),
                summary.getImpression(),
                summary.getPrescription()
        );
    }
}
