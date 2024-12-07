package back.service;

import back.controller.dto.UserDTO;
import back.model.Role;
import back.model.Token;
import back.model.User;
import back.model.UserRole;
import back.repository.RoleRepository;
import back.repository.UserRepository;
import back.repository.UserRoleRepository;
import back.security.JwtIssuer;
import back.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtIssuer jwtIssuer;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public Token attemptLogin(String email, String password){
        System.out.println("Email użytkownika: " + email);
        System.out.println("Zakodowane hasło użytkownika: " + password);

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var principal = (UserPrincipal) authentication.getPrincipal();

        var role = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        var token = jwtIssuer.issue(principal.getUserId(), principal.getEmail(), principal.getName(), role);
        return Token
                .accessToken(token)
                .build();
    }

    public Token registerUser(UserDTO userDTO) {
        System.out.println("Email użytkownika: " + userDTO.getEmail());
        System.out.println("Zakodowane hasło użytkownika: " + userDTO.getPassword());


        // Sprawdź, czy użytkownik już istnieje
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Użytkownik o podanym adresie e-mail już istnieje");
        }

        if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        User newUser = new User();
        newUser.setEmail(userDTO.getEmail());
        newUser.setPassword(encodedPassword);
        newUser.setName(userDTO.getName());

        User savedUser = userRepository.save(newUser);

        Role defaultRole = roleRepository.findByRoleTitle("USER")
                .orElseThrow(() -> new IllegalArgumentException("Domyślna rola nie istnieje"));

        UserRole userRole = new UserRole();
        userRole.setUserId(savedUser.getUserId());
        userRole.setRoleId(defaultRole.getRoleId());

        userRoleRepository.save(userRole);

        // Automatyczne zalogowanie nowo zarejestrowanego użytkownika
        return attemptLogin(userDTO.getEmail(), userDTO.getPassword());
    }
}
