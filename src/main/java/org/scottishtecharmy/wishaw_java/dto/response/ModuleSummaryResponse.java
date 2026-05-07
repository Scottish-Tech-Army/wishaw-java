package org.scottishtecharmy.wishaw_java.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ModuleSummaryResponse {
    private Long id;
    private String name;
    private String gameName;
    private String description;
    private boolean active;
    private boolean approved;
}
