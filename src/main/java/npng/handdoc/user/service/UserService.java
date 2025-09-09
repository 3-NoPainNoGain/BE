package npng.handdoc.user.service;

import lombok.RequiredArgsConstructor;
import npng.handdoc.user.domain.User;
import npng.handdoc.user.domain.type.LoginType;
import npng.handdoc.user.exception.UserException;
import npng.handdoc.user.exception.errorcode.UserErrorCode;
import npng.handdoc.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                    return userRepository.save(user);
                });
    }

    @Transactional
    public void addName(Long userId, String name){
        User user = findUserOrThrow(userId);
        user.addName(name);
    }

    private User findUserOrThrow(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(()-> new UserException(UserErrorCode.USER_NOT_FOUND));
    }
}
