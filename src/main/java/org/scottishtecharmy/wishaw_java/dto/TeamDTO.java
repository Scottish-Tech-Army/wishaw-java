package org.scottishtecharmy.wishaw_java.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TeamDTO {
    private Long id;

    @NotBlank(message = "Team name is required")
    private String name;

    @NotNull(message = "Tournament ID is required")
    private Long tournamentId;

    private String tournamentName;
    private Long captainId;
    private String captainName;
    private List<Long> memberIds;
    private List<UserResponseDTO> members;
    private String organization;
    private String createdAt;
}

