package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.*;
import org.scottishtecharmy.wishaw_java.model.Student;
import org.scottishtecharmy.wishaw_java.repository.StudentRepository;
import org.scottishtecharmy.wishaw_java.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(StudentRepository studentRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseDto login(LoginRequestDto request) {
        Student student = studentRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password."));

        if (!passwordEncoder.matches(request.getPassword(), student.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password.");
        }

        String token = jwtService.generateToken(student);
        return LoginResponseDto.builder().token(token).build();
    }

    public void logout() {
        // Stateless JWT — nothing to invalidate server-side.
        // If a token blacklist were required, it would be handled here.
    }

    public ForgotUsernameResponseDto forgotUsername(ForgotUsernameRequestDto request) {
        Student student = studentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No account found for that email."));
        return new ForgotUsernameResponseDto(student.getUsername());
    }

    public ForgotPasswordResponseDto forgotPassword(ForgotPasswordRequestDto request) {
        Student student = studentRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No account found for that username."));
        String hint = student.getPasswordHint() != null ? student.getPasswordHint() : "No hint available.";
        return new ForgotPasswordResponseDto(hint);
    }
}
