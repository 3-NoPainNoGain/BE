package npng.handdoc.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import npng.handdoc.reservation.domain.type.Option;
import npng.handdoc.reservation.domain.type.Symptom;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record ReservationCreateRequest(
        @NotNull
        @Schema(description = "의사 프로필 id")
        Long doctorProfileId,

        @NotNull
        @Schema(description = "예약 날짜", example = "2025-09-20")
        LocalDate slotDate,

        @NotNull
        @Schema(description = "시작 시간", example = "14:00:00")
        LocalTime startTime,

        @NotNull
        @Schema(description = "종료 시간", example = "14:20:00")
        LocalTime endTime,

        @NotNull
        @Schema(description = "증상", example = "HEADACHE")
        Symptom symptom,

        @Schema(description = "증상 지속 시간", example = "2")
        Long symptomDuration,

        @Schema(description = "기타 증상", example = "머리가 어지러워요")
        String description,

        @Schema(description = "통역 옵션", example = "[\"SIGN_TO_TEXT\",\"VOICE_TO_TEXT\"]")
        Set<Option> interpretationOption
) {}
