package org.scottishtecharmy.wishaw_java.dto;

import com.ltc.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.scottishtecharmy.wishaw_java.enums.OrganizationType;
import org.scottishtecharmy.wishaw_java.enums.ParticipationType;
import org.scottishtecharmy.wishaw_java.enums.SportType;
import org.scottishtecharmy.wishaw_java.enums.TournamentStatus;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TournamentDTO {
    private Long id;

    @NotBlank(message = "Tournament name is required")
    private String name;

    private String description;

    @NotNull(message = "Sport type is required")
    private SportType sportType;

    @NotNull(message = "Participation type is required")
    private ParticipationType participationType;

    @NotNull(message = "Organization type is required")
    private OrganizationType organizationType;

    private TournamentStatus status;
    private Integer maxParticipants;
    private String registrationStartDate;
    private String registrationEndDate;
    private String startDate;
    private String endDate;
    private String location;
    private String rules;
    private Long createdById;
    private String createdByName;
    private String createdAt;
    private String updatedAt;
    private Long registrationCount;
}

