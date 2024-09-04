package back.model;

import lombok.*;
import org.springframework.web.bind.annotation.RequestBody;

@Getter
@Setter
@Builder
public class Token {
    private String token;

    public static Token.TokenBuilder accessToken(String token) {
        return Token.builder().token(token);
    }
}
