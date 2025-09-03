package npng.handdoc.user.domain.type;

import lombok.Getter;

@Getter
public enum Speciality {
    INTERNAL("내과"),
    GENERAL("외과"),
    PEDIATIRCS("소아과"),
    FAMILY("가정의학과");

    private final String label;

    Speciality(String label){
        this.label = label;
    }
}
