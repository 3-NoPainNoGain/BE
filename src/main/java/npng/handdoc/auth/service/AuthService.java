package npng.handdoc.auth.service;

import lombok.RequiredArgsConstructor;
import npng.handdoc.auth.dto.request.BasicLoginRequest;
import npng.handdoc.auth.dto.response.LoginResponse;
import npng.handdoc.auth.exception.AuthException;
import npng.handdoc.auth.service.strategy.SocialLoginStrategy;
import npng.handdoc.auth.util.JwtTokenProvider;
import npng.handdoc.user.domain.User;
import npng.handdoc.user.domain.type.LoginType;
import npng.handdoc.user.exception.UserException;
import npng.handdoc.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import static npng.handdoc.auth.exception.errorcode.AuthErrorCode.LOGIN_TYPE_NOT_SUPPORTED;
import static npng.handdoc.user.exception.errorcode.UserErrorCode.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, SocialLoginStrategy> loginStrategyMap;

    public LoginResponse socialLogin(LoginType loginType, String code) {
        SocialLoginStrategy loginStrategy = loginStrategyMap.get(loginType.name());

        if (loginStrategy == null) {
            throw new AuthException(LOGIN_TYPE_NOT_SUPPORTED);
        }

        return loginStrategy.login(code);
    }
    public void signup(BasicLoginRequest request) {
        String email = request.email();

        if (userRepository.existsByEmail(email)) {
            throw new UserException(USER_ALREADY_EXISTS);
        }

        userRepository.save(User.basicLoginBuilder()
                        .email(email)
                        .password(passwordEncoder.encode(request.password()))
                        .buildBasicLogin());
    }

    public LoginResponse login(BasicLoginRequest request) {
        User user =
                userRepository
                        .findByEmail(request.email())
                        .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UserException(INVALID_CREDENTIALS);
        }

        String token = jwtTokenProvider.createToken(user.getId().toString());
        return LoginResponse.from(user.getNickname(), user.getRole(), token);
    }
}
