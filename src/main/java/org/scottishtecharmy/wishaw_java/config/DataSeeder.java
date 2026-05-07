package org.scottishtecharmy.wishaw_java.config;

import org.scottishtecharmy.wishaw_java.entity.User;
import org.scottishtecharmy.wishaw_java.enums.UserRole;
import org.scottishtecharmy.wishaw_java.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedData(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .email("admin@ltc.com")
                        .fullName("System Admin")
                        .role(UserRole.ADMIN)
                        .organization("LTC")
                        .build();
                userRepository.save(admin);

                User player1 = User.builder()
                        .username("player1")
                        .password(passwordEncoder.encode("player123"))
                        .email("player1@ltc.com")
                        .fullName("John Doe")
                        .role(UserRole.PLAYER)
                        .organization("LTC")
                        .build();
                userRepository.save(player1);

                User player2 = User.builder()
                        .username("player2")
                        .password(passwordEncoder.encode("player123"))
                        .email("player2@ltc.com")
                        .fullName("Jane Smith")
                        .role(UserRole.PLAYER)
                        .organization("LTC")
                        .build();
                userRepository.save(player2);

                log.info("Seeded default users: admin, player1, player2");
            }
        };
    }
}
