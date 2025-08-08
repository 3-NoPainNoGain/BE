package npng.handdoc.diagnosis.excpetion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class DiagnosisException extends RuntimeException {
    private final ErrorCode errorCode;
}
