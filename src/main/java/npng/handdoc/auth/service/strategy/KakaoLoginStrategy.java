package npng.handdoc.auth.service.strategy;

import lombok.RequiredArgsConstructor;
import npng.handdoc.auth.dto.response.LoginResponse;
import npng.handdoc.auth.dto.response.kakao.KakaoUserInfoResponse;
import npng.handdoc.auth.util.JwtTokenProvider;
import npng.handdoc.auth.util.kakao.KakaoApiClient;
import npng.handdoc.user.domain.User;
import npng.handdoc.user.domain.type.LoginType;
import npng.handdoc.user.service.UserService;
import org.springframework.stereotype.Service;

@Service("KAKAO")
@RequiredArgsConstructor
public class KakaoLoginStrategy implements SocialLoginStrategy {

    private final KakaoApiClient kakaoApiClient;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public LoginResponse login(String code) {
        // 1. Authorization Code를 Access Token으로 교환
        String accessToken = kakaoApiClient.getAccessToken(code);

        // 2. Access Token을 이용해 사용자 정보를 가져오고 없으면 회원가입
        KakaoUserInfoResponse userInfo = kakaoApiClient.getUserInfo(accessToken);

        // 3. 사용자 정보로 회원가입 또는 로그인
        User user = userService.findOrCreateUser(userInfo.kakao_account().email(), LoginType.KAKAO);

        // 4. JWT 토큰 생성
        String token = jwtTokenProvider.createToken(user.getId().toString());

        return LoginResponse.from(user.getName(), user.getRole(), token);
    }
}