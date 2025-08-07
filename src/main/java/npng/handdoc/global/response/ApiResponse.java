package npng.handdoc.global.response;

import lombok.Builder;

import java.util.Collections;

@Builder
public record ApiResponse<T>(Boolean isSuccess, String code, String message, T results) {
    public static final ApiResponse<Object> EMPTY_RESPONSE =
            ApiResponse.builder()
                    .isSuccess(true)
                    .code("REQUEST_OK")
                    .message("요청이 승인되었습니다.")
                    .results(Collections.emptyMap())
                    .build();

    public static final ApiResponse<Object> JSON_ROLE_ERROR =
            ApiResponse.builder()
                    .isSuccess(false)
                    .code("JSON_ROLE_ERROR")
                    .message("가진 권한으로는 실행할 수 없는 기능입니다.")
                    .results(Collections.emptyMap())
                    .build();

    public static final ApiResponse<Object> JSON_AUTH_ERROR =
            ApiResponse.builder()
                    .isSuccess(false)
                    .code("JSON_AUTH_ERROR")
                    .message("로그인 후 다시 접근해주시기 바랍니다.")
                    .results(Collections.emptyMap())
                    .build();

    public static <T> ApiResponse<T> from(T dto) {
        return ApiResponse.builder()
                .isSuccess(true)
                .code("REQUEST_OK")
                .message("요청이 완료되었습니다.")
                .results(dto)
                .build();
    }
}