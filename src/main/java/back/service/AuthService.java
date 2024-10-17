package back.service;

import back.controller.dto.UserDTO;
import back.model.Token;
import back.model.User;
import back.repository.UserRepository;
import back.security.JwtIssuer;
import back.security.UserPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtIssuer jwtIssuer;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Token attemptLogin(String email, String password){
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var principal = (UserPrincipal) authentication.getPrincipal();

        var roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        var token = jwtIssuer.issue(principal.getUserId(), principal.getEmail(), principal.getName(), roles.toString());
        return Token
                .accessToken(token)
                .build();
    }

    public Token registerUser(UserDTO userDTO) {
        // Sprawdź, czy użytkownik już istnieje
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Użytkownik o podanym adresie e-mail już istnieje");
        }

        // Zaszyfruj hasło użytkownika
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        // Stwórz nowego użytkownika i zapisz w bazie danych
        User newUser = new User();
        newUser.setEmail(userDTO.getEmail());
        newUser.setPassword(encodedPassword);
        newUser.setName(userDTO.getName()); // Jeśli UserDTO zawiera imię

        userRepository.save(newUser);

        // Automatyczne zalogowanie nowo zarejestrowanego użytkownika
        return attemptLogin(userDTO.getEmail(), userDTO.getPassword());
    }
}
