package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminDashboardDTO {
    private Long totalTournaments;
    private Long activeTournaments;
    private Long totalPlayers;
    private Long totalMatches;
    private Long completedMatches;
    private Long upcomingMatches;
    private Long totalRegistrations;
    private Long totalTeams;
}

