package npng.handdoc.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class UserException extends RuntimeException {
    private final ErrorCode errorCode;
}
