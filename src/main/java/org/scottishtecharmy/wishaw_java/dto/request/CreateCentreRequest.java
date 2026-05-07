package org.scottishtecharmy.wishaw_java.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCentreRequest {
    @NotBlank(message = "Centre name is required")
    private String name;

    @NotBlank(message = "Centre code is required")
    private String code;
}
