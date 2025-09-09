package npng.handdoc.user.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import npng.handdoc.global.entity.BaseEntity;
import npng.handdoc.user.domain.type.LoginType;
import npng.handdoc.user.domain.type.Role;

@Getter
@Entity
@Table(name="users")
@RequiredArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "resident_id", nullable = true)
    private String residentId;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name="email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "login_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private DoctorProfile doctorProfile;

    public void attachDoctor(DoctorProfile doctor) {
        this.doctorProfile = doctor;
        if (doctor != null) doctor.setUser(this);
    }

    public void addName(String name){
        this.name = name;
    }

    public void addResidentId(String residentId){
        this.name = name;
    }

    @Builder(builderMethodName = "basicLoginBuilder", buildMethodName = "buildBasicLogin")
    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.loginType = LoginType.BASIC;
        this.role = Role.ROLE_DOCTOR;
    }

    @Builder(builderMethodName = "socialLoginBuilder", buildMethodName = "buildSocialLogin")
    public User(String email, LoginType loginType) {
        this.email = email;
        this.loginType = loginType;
        this.role = Role.ROLE_PATIENT;
    }
}
