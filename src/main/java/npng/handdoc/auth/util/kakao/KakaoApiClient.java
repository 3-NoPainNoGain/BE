package npng.handdoc.auth.util.kakao;

import lombok.RequiredArgsConstructor;
import npng.handdoc.auth.dto.response.kakao.KakaoLoginResponse;
import npng.handdoc.auth.dto.response.kakao.KakaoUserInfoResponse;
import npng.handdoc.auth.exception.AuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static npng.handdoc.auth.exception.errorcode.AuthErrorCode.LOGIN_KAKAO_TOKEN_FAILED;
import static npng.handdoc.auth.exception.errorcode.AuthErrorCode.LOGIN_KAKAO_USERINFO_FAILED;

@RequiredArgsConstructor
@Component
public class KakaoApiClient {
    private final WebClient webClient;
    private static final String USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";
    private static final String TOKEN_REQUEST_URI = "https://kauth.kakao.com/oauth/token";

    @Value("${KAKAO_REST_API_KEY}")
    private String kakaoApiKey;

    @Value("${KAKAO_REDIRECT_URI}")
    private String kakaoRedirectUri;

    // 인가 코드 > Access Token
    public String getAccessToken(String code) {
        String requestBody =
                "grant_type=authorization_code"
                        + "&client_id="
                        + kakaoApiKey
                        + "&redirect_uri="
                        + kakaoRedirectUri
                        + "&code="
                        + code;

        return webClient
                .post()
                .uri(TOKEN_REQUEST_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(requestBody)
                .exchangeToMono(
                        response -> {
                            if (response.statusCode().is2xxSuccessful()) {
                                return response.bodyToMono(KakaoLoginResponse.class);
                            } else {
                                return response
                                        .bodyToMono(String.class)
                                        .flatMap(
                                                errorBody -> {
                                                    return Mono.error(new AuthException(LOGIN_KAKAO_TOKEN_FAILED));
                                                });
                            }
                        })
                .map(KakaoLoginResponse::accessToken)
                .block();
    }

    // Access Token > 사용자 정보 조회
    public KakaoUserInfoResponse getUserInfo(String token) {
        try {
            return webClient
                    .get()
                    .uri(USER_INFO_URI)
                    .header("Authorization", "Bearer " + token.trim())
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .retrieve()
                    .bodyToMono(KakaoUserInfoResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new AuthException(LOGIN_KAKAO_USERINFO_FAILED);
        }
    }
}