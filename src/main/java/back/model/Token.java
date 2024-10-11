package back.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Token {
    private String token;

    public static Token.TokenBuilder accessToken(String token) {
        return Token.builder().token(token);
    }
}
