package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private String token;
}

