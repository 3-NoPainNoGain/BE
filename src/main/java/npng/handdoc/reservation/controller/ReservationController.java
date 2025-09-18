package npng.handdoc.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.response.ApiResponse;
import npng.handdoc.reservation.dto.request.ReservationAcceptRequest;
import npng.handdoc.reservation.dto.request.ReservationCreateRequest;
import npng.handdoc.reservation.service.ReservationService;
import npng.handdoc.user.util.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/reservation")
@Tag(name = "Reservation", description = "진료 예약 API")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "환자 진료 예약 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createReservation(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody ReservationCreateRequest request
    ) {
        Long patientUserId   = principal.getId();
        Long doctorProfileId = request.doctorProfileId();
        var dto = reservationService.create(patientUserId, doctorProfileId, request);
        return ResponseEntity.ok(ApiResponse.from(dto));
    }

    @Operation(summary = "환자 진료 예약 취소")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<Object>> cancelReservation(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long reservationId
    ) {
        reservationService.cancel(reservationId, principal.getId());
        return ResponseEntity.ok(ApiResponse.EMPTY_RESPONSE);
    }

    @Operation(summary = "의사 진료 예약 전체 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> listForDoctor(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "0")  @Min(0) int page
    ) {
        Long doctorUserId = principal.getId(); // ★ userId만 전달
        var dto = reservationService.listForDoctor(doctorUserId, size, page);
        return ResponseEntity.ok(ApiResponse.from(dto));
    }

    @Operation(summary = "진료 예약 단건 상세 조회")
    @GetMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<Object>> getOne(@PathVariable Long reservationId) {
        return ResponseEntity.ok(ApiResponse.from(reservationService.getOne(reservationId)));
    }

    @Operation(summary = "의사 예약 수락/거부")
    @PostMapping("/{reservationId}/accept")
    public ResponseEntity<ApiResponse<Object>> acceptOrDeny(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long reservationId,
            @Valid @RequestBody ReservationAcceptRequest request
    ) {
        Long doctorUserId = principal.getId(); // ★ userId만 전달
        var dto = reservationService.acceptOrDeny(reservationId, doctorUserId, request);
        return ResponseEntity.ok(ApiResponse.from(dto));
    }
}
