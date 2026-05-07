package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequestDto {
    private String username;
    private String gamertag;
    private String bio;
}
