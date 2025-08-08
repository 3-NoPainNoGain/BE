package npng.handdoc.diagnosis.dto.response;

public record StartDiagnosisRes(String DiagnosisId) {

    public static StartDiagnosisRes of(String DiagnosisId) {
        return new StartDiagnosisRes(DiagnosisId);
    }
}
