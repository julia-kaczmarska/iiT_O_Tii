package back.security;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtToPrincipalConverter {
    public UserPrincipal convert(DecodedJWT jwt){
        return UserPrincipal.builder()
                .userId(Long.valueOf(jwt.getSubject()))
                .email(jwt.getClaim("email").asString())
                .authorities(extractAuthoritiesFromClaim(jwt))
                .build();
    }

    private List<SimpleGrantedAuthority> extractAuthoritiesFromClaim(DecodedJWT jwt){
//        return jwt.getClaim("role").asList(String.class).stream()
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
        Claim roleClaim = jwt.getClaim("role");

        if (roleClaim == null) {
            throw new IllegalArgumentException("Claim 'role' is missing in the token");
        }

        List<String> roles = roleClaim.asList(String.class);

        if (roles == null) {
            throw new IllegalArgumentException("Claim 'role' is not a list of strings");
        }

        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}