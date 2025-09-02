package npng.handdoc.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.entity.BaseEntity;
import npng.handdoc.user.domain.type.DoctorStatus;
import npng.handdoc.user.domain.type.Speciality;

@Getter
@Entity
@Table(name="doctor")
@RequiredArgsConstructor
public class Doctor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = true)
    private String name;

    @Column(name="speciality")
    @Enumerated(EnumType.STRING)
    private Speciality speciality;

    @Column(name="hospital_name")
    private String hospitalName;

    @Column(name="introducation")
    private String introduction;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private DoctorStatus status;

    @OneToOne(mappedBy = "doctor")
    private User user;
}
