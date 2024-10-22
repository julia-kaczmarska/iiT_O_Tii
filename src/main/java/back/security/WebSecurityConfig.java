package back.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailService customUserDetailService;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); //options - przeglądarka wysyła żądania preflight używając OPTIONS
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }


    @Bean
    public SecurityFilterChain applicationsecurity(HttpSecurity http) throws Exception {
//        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        http
//                .cors().disable()
//                .csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .formLogin().disable()              //pozbywa sie logowania na 8080
//                .securityMatcher("/**")
//                .authorizeHttpRequests(registry -> registry
////                        .requestMatchers("/").permitAll()
//                        .requestMatchers("/auth/**").permitAll()
////                        .requestMatchers("/user/**").permitAll()
////                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .anyRequest().authenticated()
//
//                );
//
//        return http.build();

        http
            .cors().and() // Włącz CORS
            .csrf().disable() // Wyłącz CSRF
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Bezstanowe sesje
            .and()
            .formLogin().disable() // Wyłącz logowanie
            .authorizeHttpRequests(registry -> registry
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Pozwól na wszystkie zapytania OPTIONS (preflight)
                    .requestMatchers("/auth/**").permitAll() // Zezwól na dostęp do endpointów autoryzacji
                    .anyRequest().authenticated() // Wymagaj autoryzacji dla pozostałych żądań
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(customUserDetailService)
                .passwordEncoder(passwordEncoder())
                .and().build();
    }
}
