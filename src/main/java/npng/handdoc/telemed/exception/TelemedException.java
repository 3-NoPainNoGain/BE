package npng.handdoc.telemed.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class TelemedException extends RuntimeException {
    private final ErrorCode errorCode;
}
