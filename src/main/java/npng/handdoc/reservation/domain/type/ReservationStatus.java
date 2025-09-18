package npng.handdoc.reservation.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReservationStatus {
    REQUESTED("예약 확인 중"),
    CONFIRMED("예약 완료"),
    CANCELED("예약 취소"),
    COMPLETED("진료 완료");

    private final String label;

    public String getLabel() { return label; }
}
