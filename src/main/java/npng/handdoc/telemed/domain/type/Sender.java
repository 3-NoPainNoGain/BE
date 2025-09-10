package npng.handdoc.telemed.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Sender {
    DOCTOR("의사"),
    PATIENT("환자");

    private final String label;

    public String getLabel() {
        return label;
    }
}
