package org.scottishtecharmy.wishaw_java.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEnrollmentStatusRequest {
    @NotBlank(message = "Enrollment status is required")
    private String status;
}