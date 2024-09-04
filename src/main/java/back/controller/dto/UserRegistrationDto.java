package back.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public class UserRegistrationDto {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
//    private final List<String> roles;
}