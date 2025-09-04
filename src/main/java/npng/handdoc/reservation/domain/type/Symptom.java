package npng.handdoc.reservation.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Symptom {
    COUGH("기침"),
    SPUTUM("가래"),
    NOSE("코막힘"),
    HEADACHE("두통"),
    FEVER("발열");

    private final String label;

    public String getLabel() {
        return label;
    }
}
