package npng.handdoc.reservation.domain.type;

import lombok.Getter;

@Getter
public enum ReservationStatus {
    PENDING("예약 확인 중"),
    CONFIRMED("예약 완료");

    private final String label;

    ReservationStatus(String label) {
        this.label = label;
    }
}
