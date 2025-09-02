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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name="patient_id", unique = true)
    private Patient patient;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name="doctor_id", unique = true)
    private Doctor doctor;

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
