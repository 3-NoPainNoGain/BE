package npng.handdoc.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "예약 목록 응답")
public record ReservationListResponse(
        List<ReservationItemResponse> items,
        long totalElements,
        int totalPages,
        int page,
        int size,
        boolean hasNext
) {
    public static ReservationListResponse from(Page<ReservationItemResponse> page){
        return new ReservationListResponse(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                page.hasNext()
        );
    }
}
