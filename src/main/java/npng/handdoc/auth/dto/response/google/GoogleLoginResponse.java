package npng.handdoc.auth.dto.response.google;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleLoginResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") int expiresIn,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("scope") String scope,
        @JsonProperty("refresh_token") String refreshToken) {}