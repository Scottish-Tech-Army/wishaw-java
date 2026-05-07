package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.NotificationDtos;
import org.scottishtecharmy.wishaw_java.entity.NotificationRecord;
import org.scottishtecharmy.wishaw_java.entity.Tournament;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.enums.NotificationType;
import org.scottishtecharmy.wishaw_java.enums.UserRole;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.mapper.ApiMapper;
import org.scottishtecharmy.wishaw_java.repository.NotificationRecordRepository;
import org.scottishtecharmy.wishaw_java.repository.TournamentRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class NotificationService {

    private final NotificationRecordRepository notificationRecordRepository;
    private final TournamentRepository tournamentRepository;
    private final UserAccountRepository userAccountRepository;
    private final ApiMapper apiMapper;

    public NotificationService(NotificationRecordRepository notificationRecordRepository,
                               TournamentRepository tournamentRepository,
                               UserAccountRepository userAccountRepository,
                               ApiMapper apiMapper) {
        this.notificationRecordRepository = notificationRecordRepository;
        this.tournamentRepository = tournamentRepository;
        this.userAccountRepository = userAccountRepository;
        this.apiMapper = apiMapper;
    }

    @Transactional(readOnly = true)
    public NotificationDtos.NotificationListResponse getNotifications(UserAccount currentUser) {
        List<NotificationDtos.NotificationDto> notifications = notificationRecordRepository.findByUserAccount_IdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .map(apiMapper::toNotificationDto)
                .toList();
        int unreadCount = (int) notifications.stream().filter(notification -> !notification.isRead()).count();
        return new NotificationDtos.NotificationListResponse(notifications, unreadCount);
    }

    public void markRead(String id, UserAccount currentUser) {
        NotificationRecord notificationRecord = notificationRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!notificationRecord.getUserAccount().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Notification not found");
        }
        notificationRecord.setRead(true);
        notificationRecordRepository.save(notificationRecord);
    }

    public void markAllRead(UserAccount currentUser) {
        List<NotificationRecord> notifications = notificationRecordRepository.findByUserAccount_IdOrderByCreatedAtDesc(currentUser.getId());
        notifications.forEach(notification -> notification.setRead(true));
        notificationRecordRepository.saveAll(notifications);
    }

    public Map<String, Boolean> createAnnouncement(NotificationDtos.AnnouncementRequest request) {
        String linkTo = request.tournamentId() == null ? null : "/tournaments/" + request.tournamentId();
        notificationRecordRepository.saveAll(userAccountRepository.findAll().stream()
            .map(userAccount -> NotificationRecord.builder()
                        .id("n-" + UUID.randomUUID())
                .userAccount(userAccount)
                        .type(NotificationType.ANNOUNCEMENT)
                        .title(request.title())
                        .message(request.message())
                        .isRead(false)
                        .createdAt(Instant.now())
                        .linkTo(linkTo)
                        .build())
                .toList());
        return Map.of("success", true);
    }

            public void notifyTournamentPublished(Tournament tournament) {
            String linkTo = "/tournaments/" + tournament.getId();
            String sportName = tournament.getSport() == null ? "Tournament" : tournament.getSport().getName();
            notificationRecordRepository.saveAll(userAccountRepository.findAll().stream()
                .filter(userAccount -> userAccount.getRole() == UserRole.PLAYER)
                .map(userAccount -> NotificationRecord.builder()
                    .id("n-" + UUID.randomUUID())
                    .userAccount(userAccount)
                    .type(NotificationType.TOURNAMENT)
                    .title("Tournament Published")
                    .message(tournament.getName() + " for " + sportName + " is now open for registration.")
                    .isRead(false)
                    .createdAt(Instant.now())
                    .linkTo(linkTo)
                    .build())
                .toList());
            }

            public void notifyAdminsOfRegistration(Tournament tournament, String participantName) {
            String linkTo = "/admin/tournaments/" + tournament.getId();
            notificationRecordRepository.saveAll(userAccountRepository.findAll().stream()
                .filter(userAccount -> userAccount.getRole() == UserRole.ADMIN || userAccount.getRole() == UserRole.SUPER_ADMIN)
                .map(userAccount -> NotificationRecord.builder()
                    .id("n-" + UUID.randomUUID())
                    .userAccount(userAccount)
                    .type(NotificationType.TOURNAMENT)
                    .title("New Tournament Registration")
                    .message(participantName + " joined " + tournament.getName() + ".")
                    .isRead(false)
                    .createdAt(Instant.now())
                    .linkTo(linkTo)
                    .build())
                .toList());
            }

    @Transactional(readOnly = true)
    public List<NotificationDtos.AnnouncementDto> getAnnouncements(String tournamentId) {
        return notificationRecordRepository.findByTypeOrderByCreatedAtDesc(NotificationType.ANNOUNCEMENT).stream()
                .filter(notification -> tournamentId == null || (notification.getLinkTo() != null && notification.getLinkTo().endsWith(tournamentId)))
                .map(notification -> new NotificationDtos.AnnouncementDto(notification.getTitle(), notification.getMessage(), notification.getCreatedAt().toString()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Object> getGallery(String tournamentId) {
        tournamentRepository.findById(tournamentId).orElseThrow(() -> new ResourceNotFoundException("Tournament not found"));
        return List.of();
    }

    @Transactional(readOnly = true)
    public NotificationDtos.ShareDataDto getShareData(String type, String id) {
        return switch (type.toLowerCase()) {
            case "tournament" -> tournamentRepository.findById(id)
                    .map(tournament -> new NotificationDtos.ShareDataDto(
                            tournament.getName(),
                            tournament.getDescription(),
                            "/tournaments/" + tournament.getId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Tournament not found"));
            default -> new NotificationDtos.ShareDataDto("Wishaw eSports", "Check this out", "/");
        };
    }
}
