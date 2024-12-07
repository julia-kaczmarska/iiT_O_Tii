package back.controller.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class UserDTO {
    private String email;
    private String password;
    private String name;
    private final List<String> role;

}