package npng.handdoc.diagnosis.dto.response;

public record SttResultRes(String text) {
    public static SttResultRes of(String text) {
        return new SttResultRes(text);
    }
}
