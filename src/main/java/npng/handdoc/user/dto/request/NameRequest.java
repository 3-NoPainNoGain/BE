package npng.handdoc.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record NameRequest(@NotBlank(message = "이름은 비어 있을 수 없습니다.")String name) {
}
