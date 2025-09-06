package npng.handdoc.user.repository;

import npng.handdoc.user.domain.User;
import npng.handdoc.user.domain.type.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findUserById(Long id);
    Optional<User> findByEmailAndLoginType(String email, LoginType loginType);
    boolean existsByEmail(String email);
}
