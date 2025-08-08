package npng.handdoc.diagnosis.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SignLogReq(
        @NotBlank String message
) {
}
