package npng.handdoc.diagnosis.dto.response;

public record SummaryAIRes(
        String symptom,
        String impression,
        String prescription
) {}
