package npng.handdoc.user.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DoctorStatus {
    ENABLED("진료 가능"),
    DISABLED("진료 종료");

    private final String label;

    public String getLabel() {
        return label;
    }
}
