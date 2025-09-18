package npng.handdoc.reservation.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode implements ErrorCode {
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약이 존재하지 않습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INVALID_TIME_RANGE(HttpStatus.BAD_REQUEST, "시작/종료 시간이 올바르지 않습니다."),
    ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "이미 종료된 예약입니다."),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "허용되지 않는 상태 전이입니다.")

    ;

    private final HttpStatus httpStatus;
    private final String message;
}