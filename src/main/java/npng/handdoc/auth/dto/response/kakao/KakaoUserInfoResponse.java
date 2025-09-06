package npng.handdoc.auth.dto.response.kakao;

public record KakaoUserInfoResponse(Long id, KakaoAccount kakao_account) {
    public record KakaoAccount(String email) {}
}
