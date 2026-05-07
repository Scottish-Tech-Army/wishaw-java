package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

/**
 * Request body for creating a new student user.
 * POST /api/v1/admin/users
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequestDto {
    private String username;
    private String password;
    private String name;
    private String gamertag;
    private String centre;
    private String group;
}
