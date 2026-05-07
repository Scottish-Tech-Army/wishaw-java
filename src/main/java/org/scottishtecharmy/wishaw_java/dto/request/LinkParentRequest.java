package org.scottishtecharmy.wishaw_java.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkParentRequest {
    @NotNull(message = "Parent user ID is required")
    private Long parentUserId;

    @NotNull(message = "Player user ID is required")
    private Long playerUserId;

    private String relationshipLabel;
}
