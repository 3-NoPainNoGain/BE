package npng.handdoc.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class AuthException extends RuntimeException {
    private final ErrorCode errorCode;
}
