package org.scottishtecharmy.wishaw_java.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateChallengeRequest {
    @NotBlank(message = "Challenge name is required")
    private String name;

    private String description;

    @NotNull(message = "Badge category ID is required")
    private Long badgeCategoryId;

    @Min(value = 0, message = "Points must be zero or positive")
    private int points;

    private int displayOrder;
}
