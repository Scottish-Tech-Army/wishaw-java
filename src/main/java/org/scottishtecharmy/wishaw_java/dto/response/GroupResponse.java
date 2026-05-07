package org.scottishtecharmy.wishaw_java.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GroupResponse {
    private Long id;
    private String name;
    private String gameName;
    private String ageBand;
    private Long centreId;
    private String centreName;
    private boolean active;
}
