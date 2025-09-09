package npng.handdoc.user.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 유저입니다."),
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "잘못된 계정 정보입니다."),
    DOCTOR_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 의사입니다."),
    WRONG_RESIDENT_ID(HttpStatus.BAD_REQUEST, "잘못된 주민등록번호 형식입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}