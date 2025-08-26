package npng.handdoc.diagnosis.dto.response;

public record SummaryRes(
        String consultationTime,
        String symptom,
        String impression,
        String prescription
){
    public static SummaryRes of(String consultationTime, String symptom, String impression, String prescription) {
        return new SummaryRes(consultationTime, symptom, impression, prescription);
    }
}
