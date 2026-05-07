package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotUsernameRequestDto {
    private String email;
}
