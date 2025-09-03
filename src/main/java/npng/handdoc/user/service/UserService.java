package npng.handdoc.user.service;

import lombok.RequiredArgsConstructor;
import npng.handdoc.user.domain.Patient;
import npng.handdoc.user.domain.User;
import npng.handdoc.user.domain.type.LoginType;
import npng.handdoc.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findOrCreateUser(String email, LoginType loginType) {
        return userRepository.findByEmailAndLoginType(email, loginType)
                .orElseGet(() -> {
                    User user = User.socialLoginBuilder()
                            .email(email)
                            .loginType(loginType)
                            .buildSocialLogin();
                    user.attachPatient(new Patient());
                    return userRepository.save(user);
                });
    }
}
