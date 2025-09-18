package npng.handdoc.diagnosis.dto.response;

public record SummaryAIResponse(
        String symptom,
        String impression,
        String prescription
) {
}
