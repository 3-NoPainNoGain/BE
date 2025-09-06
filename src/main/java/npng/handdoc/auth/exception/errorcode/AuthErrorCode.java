package npng.handdoc.auth.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    LOGIN_TYPE_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "지원하지 않는 로그인 타입입니다."),
    LOGIN_KAKAO_TOKEN_FAILED(HttpStatus.BAD_GATEWAY, "카카오 서버에서 액세스 토큰을 받아오는 데 실패했습니다."),
    LOGIN_KAKAO_USERINFO_FAILED(HttpStatus.BAD_GATEWAY, "카카오 서버에서 사용자 정보를 받아오는 데 실패했습니다."),
    ;
    private final HttpStatus httpStatus;
    private final String message;
}
