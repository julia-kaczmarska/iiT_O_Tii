package back.service;

import back.model.User;
import back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

//    private static final String EXISTING_EMAIL = "admin@test.com";
//    private static final String ANOTHER_EMAIL = "user@test.com";
//
//    public Optional<UserEntity> findByEmail(String email) {
//
//        if (EXISTING_EMAIL.equalsIgnoreCase(email)) {
//            var user = new UserEntity();
//            user.setEmail(EXISTING_EMAIL);
//            user.setId(1L);
//            user.setPassword("$2a$12$03omPxXsrXyFF6SwapBQUuEA9KUbtuyKgx0JmXDPaMJP04hR3lZKi"); //test
//            user.setRole("ROLE_ADMIN");
//            user.setExtraInfo("Muy admimn");
//            return Optional.of(user);
//        } else if (ANOTHER_EMAIL.equalsIgnoreCase(email)) {
//            var user = new UserEntity();
//            user.setEmail(ANOTHER_EMAIL);
//            user.setId(99L);
//            user.setPassword("$2a$12$03omPxXsrXyFF6SwapBQUuEA9KUbtuyKgx0JmXDPaMJP04hR3lZKi"); //test
//            user.setRole("ROLE_USER");
//            user.setExtraInfo("Muy user");
//            return Optional.of(user);
//        }
//
//        return Optional.empty();
//
//    }
}