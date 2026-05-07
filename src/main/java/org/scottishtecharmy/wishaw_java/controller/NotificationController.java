package org.scottishtecharmy.wishaw_java.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.scottishtecharmy.wishaw_java.config.ApiPaths;
import org.scottishtecharmy.wishaw_java.dto.NotificationDtos;
import org.scottishtecharmy.wishaw_java.service.CurrentUserService;
import org.scottishtecharmy.wishaw_java.service.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({ApiPaths.V1 + "/notifications", ApiPaths.LEGACY + "/notifications"})
@Tag(name = "Notifications", description = "Notifications, announcements, sharing, and gallery")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    public NotificationController(NotificationService notificationService, CurrentUserService currentUserService) {
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public NotificationDtos.NotificationListResponse getNotifications() {
        return notificationService.getNotifications(currentUserService.requireCurrentUser());
    }

    @PutMapping("/{id}/read")
    public Map<String, Boolean> markRead(@PathVariable String id) {
        notificationService.markRead(id, currentUserService.requireCurrentUser());
        return Map.of("success", true);
    }

    @PutMapping("/read-all")
    public Map<String, Boolean> markAllRead() {
        notificationService.markAllRead(currentUserService.requireCurrentUser());
        return Map.of("success", true);
    }

    @PostMapping("/announcements")
    public Map<String, Boolean> createAnnouncement(@RequestBody NotificationDtos.AnnouncementRequest request) {
        return notificationService.createAnnouncement(request);
    }

    @GetMapping("/announcements/tournament/{id}")
    public List<NotificationDtos.AnnouncementDto> getAnnouncements(@PathVariable String id) {
        return notificationService.getAnnouncements(id);
    }

    @GetMapping("/share/{type}/{id}")
    public NotificationDtos.ShareDataDto getShareData(@PathVariable String type, @PathVariable String id) {
        return notificationService.getShareData(type, id);
    }

    @GetMapping("/gallery/tournament/{id}")
    public List<Object> getGallery(@PathVariable String id) {
        return notificationService.getGallery(id);
    }
}
