package npng.handdoc.diagnosis.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class DiagnosisException extends RuntimeException {
    private final ErrorCode errorCode;
}
