package npng.handdoc.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import npng.handdoc.auth.dto.request.BasicLoginRequest;
import npng.handdoc.auth.dto.response.LoginResponse;
import npng.handdoc.auth.service.AuthService;
import npng.handdoc.global.response.ApiResponse;
import npng.handdoc.user.domain.type.LoginType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v2/auth")
@RestController
@Tag(name="Auth", description="인증 및 회원 관련 API")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "기본 회원가입", description = "(관리자) 이메일과 비밀번호로 회원가입합니다. '@'를 포함한 이메일과 비밀번호를 입력해주세요.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Object>> signUp(@Valid @RequestBody BasicLoginRequest basicLoginRequest) {
        authService.signup(basicLoginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
    }
    @Operation(summary = "기본 로그인", description = "(관리자) 이메일과 비밀번호로 로그인합니다. '@'를 포함한 이메일과 비밀번호를 입력해주세요.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@Valid @RequestBody BasicLoginRequest basicLoginRequest) {
        LoginResponse loginResponse = authService.login(basicLoginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(loginResponse));
    }

    @Operation(summary = "소셜 로그인 / 회원가입", description = "소셜 로그인을 진행합니다. 인가코드를 넣어주세요.")
    @PostMapping("/login/{loginType}")
    public ResponseEntity<ApiResponse<Object>> login(
            @PathVariable LoginType loginType, @RequestParam String code) {
        LoginResponse loginResponse = authService.socialLogin(loginType, code);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(loginResponse));
    }
}
