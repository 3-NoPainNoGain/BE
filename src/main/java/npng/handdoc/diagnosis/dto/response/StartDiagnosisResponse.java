package npng.handdoc.diagnosis.dto.response;

public record StartDiagnosisResponse(String diagnosisId) {

    public static StartDiagnosisResponse of(String diagnosisId) {
        return new StartDiagnosisResponse(diagnosisId);
    }
}
