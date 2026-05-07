package org.scottishtecharmy.wishaw_java.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BadgeDTO {
    private Long id;

    @NotBlank(message = "Badge name is required")
    private String name;

    private String description;
    private String iconUrl;
    private String criteria;
    private String createdAt;
}

