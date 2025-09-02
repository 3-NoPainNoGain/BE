package npng.handdoc.auth.service.strategy;

import npng.handdoc.auth.dto.response.LoginResponse;

public interface SocialLoginStrategy {
    LoginResponse login(String code);
}
