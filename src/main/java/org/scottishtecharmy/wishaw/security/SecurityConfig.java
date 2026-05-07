package org.scottishtecharmy.wishaw.security;

import org.scottishtecharmy.wishaw.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(@Lazy UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login.xhtml",
                    "/javax.faces.resource/**",
                    "/jakarta.faces.resource/**",
                    "/resources/**",
                    "/css/**",
                    "/images/**",
                    "/manifest.json",
                    "/sw.js",
                    "/h2-console/**"
                ).permitAll()
                .requestMatchers("/admin/**").hasRole("centre-admin")
                .requestMatchers("/coach/**").hasAnyRole("centre-admin", "coach")
                .requestMatchers("/player/**").hasAnyRole("centre-admin", "coach", "player", "parent")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login.xhtml")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard.xhtml", true)
                .failureUrl("/login.xhtml?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.xhtml?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    "/javax.faces.resource/**",
                    "/jakarta.faces.resource/**",
                    "/h2-console/**",
                    "/**/*.xhtml"
                )
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }
}
