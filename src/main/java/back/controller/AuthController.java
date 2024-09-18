package back.controller;

import back.controller.dto.UserDTO;
import back.model.Token;
import back.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/auth/login")
    public Token login(@RequestBody @Validated UserDTO userDTO){
        return authService.attemptLogin(userDTO.getEmail(),userDTO.getPassword());
    }
}
