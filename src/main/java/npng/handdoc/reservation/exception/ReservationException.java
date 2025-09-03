package npng.handdoc.reservation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class ReservationException extends RuntimeException {
    private final ErrorCode errorCode;
}
