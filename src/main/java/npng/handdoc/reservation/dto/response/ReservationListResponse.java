package npng.handdoc.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "예약 목록 응답")
public record ReservationListResponse(
        List<ReservationResponse> items,
        PageMeta page
) {
    @Schema(description = "페이지 정보")
    public record PageMeta(
            int size,
            int page,
            boolean hasNext,
            long totalElements,
            int totalPages
    ) {}
}
