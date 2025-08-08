package npng.handdoc.diagnosis.dto.response;

public record StartDiagnosisRes(String diagnosisId) {

    public static StartDiagnosisRes of(String diagnosisId) {
        return new StartDiagnosisRes(diagnosisId);
    }
}
