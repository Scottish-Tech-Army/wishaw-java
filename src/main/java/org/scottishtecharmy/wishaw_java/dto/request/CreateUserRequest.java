package org.scottishtecharmy.wishaw_java.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Display name is required")
    private String displayName;

    @NotNull(message = "Role is required")
    private String role;

    private Long centreId;
    private Long groupId;
    private String externalRef;
}
