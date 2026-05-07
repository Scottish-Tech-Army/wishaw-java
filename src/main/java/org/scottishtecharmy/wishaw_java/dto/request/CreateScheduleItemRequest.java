package org.scottishtecharmy.wishaw_java.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateScheduleItemRequest {
    private int weekNumber;
    private String sessionFocus;
    private Long linkedChallengeId;
    private String sessionPlanUrl;
    private String sessionSlidesUrl;
    private int displayOrder;
}
