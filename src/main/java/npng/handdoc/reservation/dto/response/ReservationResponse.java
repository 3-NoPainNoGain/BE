package npng.handdoc.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Schema(description = "예약 단건 응답")
public record ReservationResponse(
        Long id,
        Long patientId,
        Long doctorProfileId,
        String status,
        String symptom,
        Long symptomDuration,
        String description,
        LocalDate slotDate,
        LocalTime startTime,
        LocalTime endTime,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
