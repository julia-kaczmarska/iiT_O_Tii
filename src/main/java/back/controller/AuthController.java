package back.controller;

import back.controller.dto.UserDto;
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
    public Token login(@RequestBody @Validated UserDto userDto){
        return authService.attemptLogin(userDto.getEmail(),userDto.getPassword());
    }
}
