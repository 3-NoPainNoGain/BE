package npng.handdoc.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import npng.handdoc.global.entity.BaseEntity;
import npng.handdoc.user.domain.type.Role;

@Getter
@Entity
@Table(name="users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
}
