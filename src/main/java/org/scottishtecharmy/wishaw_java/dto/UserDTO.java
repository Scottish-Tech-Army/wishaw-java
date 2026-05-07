package org.scottishtecharmy.wishaw_java.dto;

import org.scottishtecharmy.wishaw_java.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDTO {
    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String fullName;

    @NotNull(message = "Role is required")
    private UserRole role;

    private String organization;
    private String profilePhotoUrl;
    private String phone;
    private String createdAt;
    private String updatedAt;
}

