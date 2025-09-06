package npng.handdoc.reservation.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReservationStatus {
    PENDING("예약 확인 중"),
    CONFIRMED("예약 완료");

    private final String label;

    public String getLabel() {
        return label;
    }
}
