package org.scottishtecharmy.wishaw_java.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateActiveStatusRequest {
    @NotNull(message = "Active flag is required")
    private Boolean active;
}