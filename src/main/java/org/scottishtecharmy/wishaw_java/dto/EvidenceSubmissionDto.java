package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvidenceSubmissionDto {
    private long id;
    private Long studentId;
    private String studentUsername;
    private String studentName;
    private String badgeName;
    private String subBadgeName;
    private String subBadgeIcon;
    private String fileName;
    private String notes;
    private String submittedAt;
    private String status;
}
