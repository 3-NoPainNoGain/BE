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

    @Column(name = "nickname", unique = true)
    private String nickname;

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

    @Builder(builderMethodName = "basicLoginBuilder", buildMethodName = "buildBasicLogin")
    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.loginType = LoginType.BASIC;
        this.role = Role.ROLE_DOCTOR;
    }
}
