package org.scottishtecharmy.wishaw_java.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private String displayName;
    private String role;
    private Long centreId;
    private Long groupId;
    private String externalRef;
}
