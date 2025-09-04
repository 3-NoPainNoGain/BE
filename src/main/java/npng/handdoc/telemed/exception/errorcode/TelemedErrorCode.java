package npng.handdoc.telemed.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TelemedErrorCode implements ErrorCode {
    NOT_PARTICIPANT(HttpStatus.FORBIDDEN, "이 예약의 환자/의사만 입장할 수 있습니다."),
    RESERVATION_NOT_CONFIRMED(HttpStatus.CONFLICT, "예약이 확정 상태가 아닙니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}