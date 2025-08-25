package npng.handdoc.diagnosis.util.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OpenAIRes {
    private String symptom;
    private String impression;
    private String prescription;
}
