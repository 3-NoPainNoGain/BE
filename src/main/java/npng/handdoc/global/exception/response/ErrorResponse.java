package npng.handdoc.global.exception.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.validation.FieldError;

import java.util.List;

@Builder
public record ErrorResponse(
        Boolean isSuccess,
        String code,
        String message,
        @JsonInclude(JsonInclude.Include.NON_EMPTY) // 유효성 오류가 있을 때만 포함
        ValidationErrors results) {

    public record ValidationErrors(List<ValidationError> validationErrors) {}

    // @Valid 오류의 경우 사용 - error 발생 이유
    @Builder
    public record ValidationError(String field, String message) {
        // Spring의 FieldError 객체를 ValidationError 객체로 변환
        public static ValidationError from(final FieldError fieldError) {
            return ValidationError.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();
        }
    }
}
