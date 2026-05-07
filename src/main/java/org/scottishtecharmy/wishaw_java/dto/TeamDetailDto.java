package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDetailDto {
    private String id;
    private String name;
    private String icon;
    private String colour;
    private String hub;
    private String founded;
    private String description;
    private String game;
    private List<TeamMemberDto> members;
}
