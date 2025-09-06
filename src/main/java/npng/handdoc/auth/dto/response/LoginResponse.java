package npng.handdoc.auth.dto.response;

import lombok.Builder;
import npng.handdoc.user.domain.type.Role;

@Builder
public record LoginResponse(String name, Role role, String accessToken) {

    public static LoginResponse from(String name, Role role, String accessToken) {
        return LoginResponse.builder().name(name).role(role).accessToken(accessToken).build();
    }
}
