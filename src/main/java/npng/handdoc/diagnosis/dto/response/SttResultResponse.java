package npng.handdoc.diagnosis.dto.response;

public record SttResultResponse(String text) {
    public static SttResultResponse of(String text) {
        return new SttResultResponse(text);
    }
}
