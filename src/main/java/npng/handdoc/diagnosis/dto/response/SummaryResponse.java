package npng.handdoc.diagnosis.dto.response;

public record SummaryResponse(
        String consultationTime,
        String symptom,
        String impression,
        String prescription
){
    public static SummaryResponse of(String consultationTime, String symptom, String impression, String prescription) {
        return new SummaryResponse(consultationTime, symptom, impression, prescription);
    }
}
