package org.scottishtecharmy.wishaw_java.group.dto;

import java.time.LocalDateTime;
import java.util.List;

public record GameGroupResponse(
        Long id,
        String name,
        Long centreId,
        String centreName,
        List<MemberSummary> members,
        LocalDateTime createdAt
) {
    public record MemberSummary(Long userId, String username, String displayName) { }
}
