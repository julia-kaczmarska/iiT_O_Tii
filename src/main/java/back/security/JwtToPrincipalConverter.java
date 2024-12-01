package back.security;

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
//        var claim = jwt.getClaim("role");
//        if (claim.isNull() || claim.isMissing()) {
//            return List.of();
//        }
//        return claim.asList(SimpleGrantedAuthority.class);

        return jwt.getClaim("roles").asList(String.class).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}