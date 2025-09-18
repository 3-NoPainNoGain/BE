package npng.handdoc.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "환자 진료 예약 생성 요청")
public record ReservationCreateRequest(
        @NotNull @Schema(description = "의사 프로필 ID", example = "101")
        Long doctorProfileId,
        @NotNull @Schema(description = "예약 날짜", example = "2025-09-20")
        LocalDate slotDate,
        @NotNull @Schema(description = "시작 시간", example = "14:00:00")
        LocalTime startTime,
        @NotNull @Schema(description = "종료 시간", example = "14:30:00")
        LocalTime endTime,
        @Schema(description = "증상(enum 문자열, 미선택 시 null)", example = "HEADACHE")
        String symptom,
        @Schema(description = "증상 지속 시간(분), 미선택 가능", example = "30")
        Long symptomDuration,
        @Schema(description = "기타 증상", example = "머리가 어지러워요")
        String description
) {}
