package npng.handdoc.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "의사 예약 수락/거부 요청")
public record ReservationAcceptRequest(
        @NotNull @Schema(description = "수락 여부", example = "true")
        Boolean accept,
        @Schema(description = "거부 사유(거부 시 선택/입력)", example = "야간 진료 불가")
        String reason
) {}
