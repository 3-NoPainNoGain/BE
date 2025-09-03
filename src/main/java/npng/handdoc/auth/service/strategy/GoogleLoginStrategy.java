package npng.handdoc.auth.service.strategy;

import lombok.RequiredArgsConstructor;
import npng.handdoc.auth.dto.response.LoginResponse;
import npng.handdoc.auth.dto.response.google.GoogleUserInfoResponse;
import npng.handdoc.auth.util.JwtTokenProvider;
import npng.handdoc.auth.util.google.GoogleApiClient;
import npng.handdoc.user.domain.User;
import npng.handdoc.user.domain.type.LoginType;
import npng.handdoc.user.service.UserService;
import org.springframework.stereotype.Service;

@Service("GOOGLE")
@RequiredArgsConstructor
public class GoogleLoginStrategy implements SocialLoginStrategy {

    private final GoogleApiClient googleApiClient;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public LoginResponse login(String code) {
        String accessToken = googleApiClient.getAccessToken(code);
        GoogleUserInfoResponse userInfo = googleApiClient.getUserInfo(accessToken);
        User user = userService.findOrCreateUser(userInfo.email(), LoginType.GOOGLE);
        String token = jwtTokenProvider.createToken(user.getId().toString());
        return LoginResponse.from(user.getName(), user.getRole(), token);
    }
}