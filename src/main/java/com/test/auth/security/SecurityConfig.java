package com.test.auth.security;

import com.test.auth.security.JWT.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Configures the HTTP security for the application.
     * <ul>
     *     <li>Disables CSRF for stateless APIs.</li>
     *     <li>Allows unauthenticated access to the {@code /user/sign-up} endpoint.</li>
     *     <li>Requires authentication for all other endpoints.</li>
     *     <li>Adds a custom JWT filter before the standard username-password filter.</li>
     * </ul>
     *
     * @param httpSecurity the {@link HttpSecurity} object to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.cors(Customizer.withDefaults()) //
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        configure -> configure
                                .requestMatchers(HttpMethod.GET,"/users/check-role").authenticated()
                                .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/users/{userId}/**").hasRole("ADMIN")
                                .requestMatchers("/auth/**").permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


}

