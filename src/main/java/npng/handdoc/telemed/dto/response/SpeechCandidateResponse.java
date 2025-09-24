package npng.handdoc.telemed.dto.response;

import java.util.List;

public record SpeechCandidateResponse(
        List<String> candidates
) {
    public static SpeechCandidateResponse from(List<String> candidates){
        return new SpeechCandidateResponse(candidates);
    }
}
