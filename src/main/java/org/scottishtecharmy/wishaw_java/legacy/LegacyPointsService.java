package org.scottishtecharmy.wishaw_java.legacy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scottishtecharmy.wishaw_java.badge.Badge;
import org.scottishtecharmy.wishaw_java.badge.BadgeRepository;
import org.scottishtecharmy.wishaw_java.legacy.dto.LegacyPointsRequest;
import org.scottishtecharmy.wishaw_java.legacy.dto.LegacyPointsResponse;
import org.scottishtecharmy.wishaw_java.user.User;
import org.scottishtecharmy.wishaw_java.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LegacyPointsService {

    private final LegacyPointsRepository legacyPointsRepository;
    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;

    public List<LegacyPointsResponse> getAll() {
        log.debug("Fetching all legacy points");
        return legacyPointsRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public LegacyPointsResponse getById(Long id) {
        LegacyPoints lp = legacyPointsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Legacy points not found: " + id));
        return toResponse(lp);
    }

    public List<LegacyPointsResponse> getByUserId(Long userId) {
        log.debug("Fetching legacy points for userId={}", userId);
        return legacyPointsRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public LegacyPointsResponse create(LegacyPointsRequest request) {
        log.info("Creating legacy points: userId={}, badgeId={}, points={}", request.userId(), request.badgeId(), request.points());

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.userId()));
        Badge badge = badgeRepository.findById(request.badgeId())
                .orElseThrow(() -> new IllegalArgumentException("Badge not found: " + request.badgeId()));

        if (legacyPointsRepository.existsByUserIdAndBadgeId(request.userId(), request.badgeId())) {
            throw new IllegalArgumentException("Legacy points already exist for this user and badge. Use update instead.");
        }

        if (request.points() < 0) {
            throw new IllegalArgumentException("Points must be non-negative");
        }

        LegacyPoints lp = new LegacyPoints();
        lp.setUser(user);
        lp.setBadge(badge);
        lp.setPoints(request.points());
        lp.setReason(request.reason());
        legacyPointsRepository.save(lp);

        log.info("Legacy points created: id={}, userId={}, badgeId={}, points={}", lp.getId(), user.getId(), badge.getId(), lp.getPoints());
        return toResponse(lp);
    }

    @Transactional
    public LegacyPointsResponse update(Long id, LegacyPointsRequest request) {
        log.info("Updating legacy points: id={}, points={}", id, request.points());

        LegacyPoints lp = legacyPointsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Legacy points not found: " + id));

        if (request.points() < 0) {
            throw new IllegalArgumentException("Points must be non-negative");
        }

        // If user or badge changed, check for duplicate
        if (!lp.getUser().getId().equals(request.userId()) || !lp.getBadge().getId().equals(request.badgeId())) {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.userId()));
            Badge badge = badgeRepository.findById(request.badgeId())
                    .orElseThrow(() -> new IllegalArgumentException("Badge not found: " + request.badgeId()));

            if (legacyPointsRepository.existsByUserIdAndBadgeId(request.userId(), request.badgeId())) {
                throw new IllegalArgumentException("Legacy points already exist for this user and badge combination.");
            }
            lp.setUser(user);
            lp.setBadge(badge);
        }

        lp.setPoints(request.points());
        lp.setReason(request.reason());
        legacyPointsRepository.save(lp);

        log.info("Legacy points updated: id={}, points={}", id, lp.getPoints());
        return toResponse(lp);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting legacy points: id={}", id);
        if (!legacyPointsRepository.existsById(id)) {
            throw new IllegalArgumentException("Legacy points not found: " + id);
        }
        legacyPointsRepository.deleteById(id);
        log.info("Legacy points deleted: id={}", id);
    }

    /**
     * Get legacy points total for a specific user and badge.
     */
    public int getLegacyPointsForUserBadge(Long userId, Long badgeId) {
        return legacyPointsRepository.findByUserIdAndBadgeId(userId, badgeId)
                .map(LegacyPoints::getPoints)
                .orElse(0);
    }

    private LegacyPointsResponse toResponse(LegacyPoints lp) {
        return new LegacyPointsResponse(
                lp.getId(),
                lp.getUser().getId(),
                lp.getUser().getUsername(),
                lp.getUser().getDisplayName(),
                lp.getBadge().getId(),
                lp.getBadge().getName(),
                lp.getPoints(),
                lp.getReason()
        );
    }
}

