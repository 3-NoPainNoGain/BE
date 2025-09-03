package npng.handdoc.user.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.entity.BaseEntity;

@Getter
@Entity
@Table(name = "doctor_tag")
@RequiredArgsConstructor
public class DoctorTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorProfile doctor;

    @Builder
    public DoctorTag(String name, DoctorProfile doctor) {
        this.name = name;
        this.doctor = doctor;
    }
}
