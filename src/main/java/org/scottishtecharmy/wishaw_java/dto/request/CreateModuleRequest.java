package org.scottishtecharmy.wishaw_java.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateModuleRequest {
    @NotBlank(message = "Module name is required")
    private String name;

    private String gameName;
    private String description;
}
