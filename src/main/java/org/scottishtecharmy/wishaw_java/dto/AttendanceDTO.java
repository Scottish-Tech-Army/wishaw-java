package org.scottishtecharmy.wishaw_java.dto;

import org.scottishtecharmy.wishaw_java.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AttendanceDTO {
    private Long id;

    @NotNull(message = "Match ID is required")
    private Long matchId;

    private String matchTitle;

    @NotNull(message = "Player ID is required")
    private Long playerId;

    private String playerName;

    @NotNull(message = "Attendance status is required")
    private AttendanceStatus status;

    private Long markedById;
    private String markedByName;
    private String createdAt;
    private String updatedAt;
}

