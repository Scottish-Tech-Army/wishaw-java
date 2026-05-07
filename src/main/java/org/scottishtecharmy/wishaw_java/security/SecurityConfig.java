package org.scottishtecharmy.wishaw_java.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Value("${spring.h2.console.enabled:false}")
    private boolean h2ConsoleEnabled;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // FRONTEND_INTEGRATION: session-based auth is used for simplicity.
    // React frontend should include credentials in requests (withCredentials: true).
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable()) // Disabled for REST API usage with separate frontend
            .exceptionHandling(eh -> eh.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/logout").permitAll();
                auth.requestMatchers(HttpMethod.GET, "/openapi.yaml").permitAll();
                auth.requestMatchers(HttpMethod.GET, "/api/v1/auth/me").authenticated();
                if (h2ConsoleEnabled) {
                    auth.requestMatchers("/h2-console/**").permitAll();
                } else {
                    auth.requestMatchers("/h2-console/**").denyAll();
                }
                auth.requestMatchers("/api/v1/admin/**").hasAnyRole("SUPER_ADMIN", "CENTRE_ADMIN");
                auth.requestMatchers("/api/v1/parent/**").hasRole("PARENT");
                auth.requestMatchers("/api/v1/me/**").hasRole("PLAYER");
                auth.requestMatchers(HttpMethod.GET, "/api/v1/players/**").hasAnyRole("SUPER_ADMIN", "CENTRE_ADMIN");
                auth.requestMatchers(HttpMethod.GET, "/api/v1/leaderboards/**").authenticated();
                auth.anyRequest().authenticated();
            })
            .authenticationProvider(authenticationProvider())
            .headers(headers -> {
                if (h2ConsoleEnabled) {
                    headers.frameOptions(fo -> fo.sameOrigin());
                }
            })
            .userDetailsService(userDetailsService);

        return http.build();
    }
}
