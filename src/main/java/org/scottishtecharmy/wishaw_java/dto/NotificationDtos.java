package org.scottishtecharmy.wishaw_java.dto;

import java.util.List;

public final class NotificationDtos {

    private NotificationDtos() {
    }

    public record NotificationDto(
            String id,
            String type,
            String title,
            String message,
            boolean isRead,
            String createdAt,
            String linkTo
    ) {
    }

    public record NotificationListResponse(List<NotificationDto> notifications, int unreadCount) {
    }

    public record AnnouncementRequest(String title, String message, String tournamentId) {
    }

    public record AnnouncementDto(String title, String message, String createdAt) {
    }

    public record ShareDataDto(String title, String description, String url) {
    }
}
