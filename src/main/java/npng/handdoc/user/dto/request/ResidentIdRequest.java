package npng.handdoc.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ResidentIdRequest(@NotBlank(message = "주민등록번호는 비어 있을 수 없습니다.")String residentId) {
}
