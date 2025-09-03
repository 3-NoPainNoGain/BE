package npng.handdoc.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.entity.BaseEntity;
import npng.handdoc.user.domain.type.DoctorStatus;
import npng.handdoc.user.domain.type.Speciality;

import java.util.ArrayList;
import java.util.List;

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

    @Column(name="introduction")
    private String introduction;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private DoctorStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorTag> doctorTagList = new ArrayList<>();

    void setUser(User user) { this.user = user; }
}
