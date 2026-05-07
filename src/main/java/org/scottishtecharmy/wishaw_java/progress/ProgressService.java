package org.scottishtecharmy.wishaw_java.progress;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scottishtecharmy.wishaw_java.badge.Badge;
import org.scottishtecharmy.wishaw_java.badge.BadgeRepository;
import org.scottishtecharmy.wishaw_java.badge.SubBadge;
import org.scottishtecharmy.wishaw_java.badge.SubBadgeRepository;
import org.scottishtecharmy.wishaw_java.legacy.LegacyPointsRepository;
import org.scottishtecharmy.wishaw_java.level.LevelService;
import org.scottishtecharmy.wishaw_java.progress.dto.BadgeProgressResponse;
import org.scottishtecharmy.wishaw_java.progress.dto.UserProfileResponse;
import org.scottishtecharmy.wishaw_java.user.User;
import org.scottishtecharmy.wishaw_java.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgressService {

    private final LevelService levelService;
    private final UserProgressRepository userProgressRepository;
    private final SubBadgeCompletionRepository subBadgeCompletionRepository;
    private final UserRepository userRepository;
    private final SubBadgeRepository subBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final LegacyPointsRepository legacyPointsRepository;

    /**
     * Award a sub-badge to a user: marks it complete and adds XP to the parent badge.
     */
    @Transactional
    public BadgeProgressResponse completeSubBadge(Long userId, Long subBadgeId) {
        log.info("Completing sub-badge: userId={}, subBadgeId={}", userId, subBadgeId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Sub-badge completion failed — user not found: userId={}", userId);
                    return new IllegalArgumentException("User not found: " + userId);
                });
        SubBadge subBadge = subBadgeRepository.findById(subBadgeId)
                .orElseThrow(() -> {
                    log.warn("Sub-badge completion failed — sub-badge not found: subBadgeId={}", subBadgeId);
                    return new IllegalArgumentException("SubBadge not found: " + subBadgeId);
                });

        // Prevent duplicate completion
        if (subBadgeCompletionRepository.existsByUserIdAndSubBadgeId(userId, subBadgeId)) {
            log.warn("Sub-badge completion failed — already completed: userId={}, subBadgeId={}", userId, subBadgeId);
            throw new IllegalArgumentException("Sub-badge already completed by this user");
        }

        // Record completion
        SubBadgeCompletion completion = new SubBadgeCompletion();
        completion.setUser(user);
        completion.setSubBadge(subBadge);
        subBadgeCompletionRepository.save(completion);

        // Add points to parent badge progress
        Badge badge = subBadge.getBadge();
        UserProgress progress = findOrCreateProgress(user, badge);
        progress.setTotalPoints(progress.getTotalPoints() + subBadge.getPoints());
        userProgressRepository.save(progress);

        log.info("Sub-badge completed: userId={}, subBadge='{}', +{}pts, totalPoints={} for badge='{}'",
                userId, subBadge.getName(), subBadge.getPoints(), progress.getTotalPoints(), badge.getName());

        int legacyPts = getLegacyPoints(userId, badge.getId());
        int combinedTotal = progress.getTotalPoints() + legacyPts;
        String level = levelService.calculateLevel(combinedTotal);
        List<BadgeProgressResponse.EarnedSubBadge> earned = subBadgeCompletionRepository
                .findByUserIdAndSubBadgeBadgeId(userId, badge.getId()).stream()
                .map(c -> new BadgeProgressResponse.EarnedSubBadge(
                        c.getSubBadge().getId(),
                        c.getSubBadge().getName(),
                        c.getSubBadge().getPoints()))
                .toList();
        return new BadgeProgressResponse(badge.getId(), badge.getName(), combinedTotal, legacyPts, level, earned);
    }

    /**
     * Get IDs of all sub-badges completed by a user.
     */
    public Set<Long> getCompletedSubBadgeIds(Long userId) {
        log.debug("Fetching completed sub-badge IDs: userId={}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return subBadgeCompletionRepository.findByUserId(userId).stream()
                .map(c -> c.getSubBadge().getId())
                .collect(Collectors.toSet());
    }

    /**
     * Get full profile for a user: all badge progress + overall XP (including legacy).
     */
    public UserProfileResponse getUserProfile(Long userId) {
        log.debug("Fetching user profile: userId={}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User profile fetch failed — not found: userId={}", userId);
                    return new IllegalArgumentException("User not found: " + userId);
                });

        List<BadgeProgressResponse> badges = badgeRepository.findAll().stream()
                .map(badge -> {
                    UserProgress progress = findOrCreateProgress(user, badge);
                    int legacyPts = getLegacyPoints(userId, badge.getId());
                    int combinedTotal = progress.getTotalPoints() + legacyPts;
                    String level = levelService.calculateLevel(combinedTotal);
                    List<BadgeProgressResponse.EarnedSubBadge> earned = subBadgeCompletionRepository
                            .findByUserIdAndSubBadgeBadgeId(userId, badge.getId()).stream()
                            .map(c -> new BadgeProgressResponse.EarnedSubBadge(
                                    c.getSubBadge().getId(),
                                    c.getSubBadge().getName(),
                                    c.getSubBadge().getPoints()))
                            .toList();
                    return new BadgeProgressResponse(badge.getId(), badge.getName(), combinedTotal, legacyPts, level, earned);
                })
                .toList();

        int overallXp = badges.stream().mapToInt(BadgeProgressResponse::totalPoints).sum();
        long completedCount = subBadgeCompletionRepository.countByUserId(userId);

        log.debug("User profile loaded: userId={}, overallXp={}, completedSubBadges={}", userId, overallXp, completedCount);

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getCentre() != null ? user.getCentre().getName() : null,
                user.getProfileImageUrl(),
                user.getDob(),
                badges,
                overallXp,
                completedCount
        );
    }

    /**
     * Get progress for a single badge for a user.
     */
    public BadgeProgressResponse getBadgeProgress(Long userId, Long badgeId) {
        log.debug("Fetching badge progress: userId={}, badgeId={}", userId, badgeId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new IllegalArgumentException("Badge not found: " + badgeId));

        UserProgress progress = findOrCreateProgress(user, badge);
        int legacyPts = getLegacyPoints(userId, badgeId);
        int combinedTotal = progress.getTotalPoints() + legacyPts;
        String level = levelService.calculateLevel(combinedTotal);
        List<BadgeProgressResponse.EarnedSubBadge> earned = subBadgeCompletionRepository
                .findByUserIdAndSubBadgeBadgeId(userId, badgeId).stream()
                .map(c -> new BadgeProgressResponse.EarnedSubBadge(
                        c.getSubBadge().getId(),
                        c.getSubBadge().getName(),
                        c.getSubBadge().getPoints()))
                .toList();
        return new BadgeProgressResponse(badge.getId(), badge.getName(), combinedTotal, legacyPts, level, earned);
    }

    private UserProgress findOrCreateProgress(User user, Badge badge) {
        return userProgressRepository.findByUserAndBadge(user, badge)
                .orElseGet(() -> {
                    log.debug("Creating new progress record: userId={}, badgeId={}", user.getId(), badge.getId());
                    UserProgress p = new UserProgress();
                    p.setUser(user);
                    p.setBadge(badge);
                    p.setTotalPoints(0);
                    return userProgressRepository.save(p);
                });
    }

    private int getLegacyPoints(Long userId, Long badgeId) {
        return legacyPointsRepository.findByUserIdAndBadgeId(userId, badgeId)
                .map(lp -> lp.getPoints())
                .orElse(0);
    }
}
