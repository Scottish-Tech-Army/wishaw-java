package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicModuleProgressDto {
    private long moduleId;
    private String moduleName;
    private String moduleIcon;
    private int sessionsCompleted;
    private int sessionsTotal;
}
