package npng.handdoc.auth.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import npng.handdoc.auth.service.AuthService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v2/auth")
@RestController
@Tag(name="Auth", description="인증 및 회원 관련 API")
public class AuthController {
    private final AuthService authService;

}
