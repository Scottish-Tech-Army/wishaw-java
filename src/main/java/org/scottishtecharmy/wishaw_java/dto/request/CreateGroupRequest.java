package org.scottishtecharmy.wishaw_java.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGroupRequest {
    @NotBlank(message = "Group name is required")
    private String name;

    private String gameName;
    private String ageBand;

    @NotNull(message = "Centre ID is required")
    private Long centreId;
}
