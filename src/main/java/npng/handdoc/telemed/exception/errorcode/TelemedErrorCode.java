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
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 방이 존재하지 않습니다."),
    ALREADY_ROOM_ENDED(HttpStatus.NOT_FOUND, "이미 종료된 방입니다."),
    SUMMARY_NOT_FOUND(HttpStatus.NOT_FOUND, "진료 요약이 존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}