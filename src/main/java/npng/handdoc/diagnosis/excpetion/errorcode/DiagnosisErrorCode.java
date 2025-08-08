package npng.handdoc.diagnosis.excpetion.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DiagnosisErrorCode implements ErrorCode {
    DIAGNOSIS_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 진료가 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
