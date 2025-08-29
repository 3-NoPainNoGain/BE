package npng.handdoc.user.util;

import lombok.RequiredArgsConstructor;
import npng.handdoc.user.domain.User;
import npng.handdoc.user.exception.UserException;
import npng.handdoc.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static npng.handdoc.user.exception.errorcode.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User user =
                userRepository
                        .findUserById(Long.parseLong(id))
                        .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        return new CustomUserDetails(user.getId(), user.getNickname(), user.getRole().name());
    }
}