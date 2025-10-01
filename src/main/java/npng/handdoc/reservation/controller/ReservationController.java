package npng.handdoc.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.response.ApiResponse;
import npng.handdoc.reservation.dto.request.ReservationDecisionRequest;
import npng.handdoc.reservation.dto.request.ReservationCreateRequest;
import npng.handdoc.reservation.service.ReservationService;
import npng.handdoc.user.util.CustomUserDetails;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/reservation")
@Tag(name = "Reservation", description = "진료 예약 API")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "(환자) 진료 예약 생성", description = "환자 로그인을 진행한 다음 호출해주세요.")
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createReservation(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                  @Valid @RequestBody ReservationCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.from(reservationService.create(userDetails.getId(), request)));
    }

    @Operation(summary = "(환자, 의사) 진료 예약 단건 상세 조회", description = "환자와 의사 모두 같은 API를 활용합니다.")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR')")
    @GetMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<Object>> getReservation(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long reservationId) {
        return ResponseEntity.ok(ApiResponse.from(reservationService.getReservation(userDetails, reservationId)));
    }

    @Operation(summary = "(환자) 진료 예약 취소", description = "환자 로그인을 진행한 다음 호출해주세요.")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<Object>> cancelReservation(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                 @PathVariable Long reservationId) {
        reservationService.cancel(reservationId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.EMPTY_RESPONSE);
    }

    @Operation(summary = "(의사) 진료 예약 전체 조회", description = "의사 로그인을 진행한 다음 호출해주세요.")
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getReservationList(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                             @RequestParam(defaultValue = "10") @Min(1) int size,
                                                             @RequestParam(defaultValue = "0")  @Min(0) int page) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.from(reservationService.getReservationList(userDetails.getId(), pageable)));
    }

    @Operation(summary = "(의사) 진료 예약 수락/거부", description = "의사 로그인을 진행한 다음 호출해주세요. true를 통해 예약을 수락하고, false를 통해 예약을 거부합니다.")
    @PostMapping("/{reservationId}/accept")
    public ResponseEntity<ApiResponse<Object>> acceptOrDeny(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @PathVariable Long reservationId,
                                                            @RequestBody ReservationDecisionRequest request) {
        reservationService.acceptOrDeny(reservationId, userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.EMPTY_RESPONSE);
    }
}
