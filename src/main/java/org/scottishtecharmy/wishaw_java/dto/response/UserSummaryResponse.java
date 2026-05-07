package org.scottishtecharmy.wishaw_java.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserSummaryResponse {
    private Long id;
    private String username;
    private String displayName;
    private String role;
    private boolean active;
    private Long centreId;
    private String centreName;
    private Long groupId;
    private String groupName;
    private String externalRef;
}
