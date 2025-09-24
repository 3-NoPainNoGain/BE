package npng.handdoc.telemed.dto.response;

import npng.handdoc.telemed.domain.Summary;

public record SummaryResponse(
        String consultationTime,
        String symptom,
        String impression,
        String prescription
) {
    public static SummaryResponse from(Summary summary) {
        return new SummaryResponse(
                summary.getConsultationTime(),
                summary.getSymptom(),
                summary.getImpression(),
                summary.getPrescription()
        );
    }
}
