package npng.handdoc.auth.util.google;

import lombok.RequiredArgsConstructor;
import npng.handdoc.auth.dto.response.google.GoogleLoginResponse;
import npng.handdoc.auth.dto.response.google.GoogleUserInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Component
public class GoogleApiClient {
    private final WebClient webClient;
    private static final String TOKEN_REQUEST_URI = "https://oauth2.googleapis.com/token";
    private static final String USER_INFO_URI = "https://www.googleapis.com/oauth2/v1/userinfo";

    @Value("${GOOGLE_CLIENT_ID}")
    private String googleApiKey;

    @Value("${GOOGLE_REDIRECT_URI}")
    private String googleRedirectUri;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String googleClientSecret;

    public String getAccessToken(String code) {
        return webClient
                .post()
                .uri(TOKEN_REQUEST_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(
                        "grant_type=authorization_code"
                                + "&client_id="
                                + googleApiKey
                                + "&client_secret="
                                + googleClientSecret
                                + "&redirect_uri="
                                + googleRedirectUri
                                + "&code="
                                + code)
                .retrieve()
                .bodyToMono(GoogleLoginResponse.class)
                .map(GoogleLoginResponse::accessToken)
                .block();
    }

    public GoogleUserInfoResponse getUserInfo(String accessToken) {
        return webClient
                .get()
                .uri(USER_INFO_URI)
                .headers(headers -> {
                            headers.setBearerAuth(accessToken);
                            headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .retrieve()
                .bodyToMono(GoogleUserInfoResponse.class)
                .block();
    }
}