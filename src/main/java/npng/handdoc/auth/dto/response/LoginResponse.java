package npng.handdoc.auth.dto.response;

import lombok.Builder;
import npng.handdoc.user.domain.type.Role;

@Builder
public record LoginResponse(String nickname, Role role, String accessToken) {

    public static LoginResponse from(String nickname, Role role, String accessToken) {
        return LoginResponse.builder().nickname(nickname).role(role).accessToken(accessToken).build();
    }
}
