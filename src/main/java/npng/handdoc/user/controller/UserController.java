package npng.handdoc.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.response.ApiResponse;
import npng.handdoc.user.dto.request.NameRequest;
import npng.handdoc.user.service.UserService;
import npng.handdoc.user.util.CustomUserDetails;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/user")
@Tag(name="User", description = "유저 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "이름 등록 API", description = "환자의 최초 회원가입 후 로그인 시 이름을 등록하는 API입니다.")
    @PostMapping("/name")
    public ResponseEntity<ApiResponse<Object>> name(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @RequestBody NameRequest request) {
        userService.addName(userDetails.getId(), request.name());
        return ResponseEntity.ok(ApiResponse.EMPTY_RESPONSE);
    }
}
