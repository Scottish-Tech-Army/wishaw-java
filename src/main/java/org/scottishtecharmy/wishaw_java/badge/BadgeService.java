package org.scottishtecharmy.wishaw_java.badge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scottishtecharmy.wishaw_java.badge.dto.BadgeRequest;
import org.scottishtecharmy.wishaw_java.badge.dto.BadgeResponse;
import org.scottishtecharmy.wishaw_java.badge.dto.SubBadgeRequest;
import org.scottishtecharmy.wishaw_java.badge.dto.SubBadgeResponse;
import org.scottishtecharmy.wishaw_java.module.Module;
import org.scottishtecharmy.wishaw_java.module.ModuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final SubBadgeRepository subBadgeRepository;
    private final ModuleRepository moduleRepository;

    // --- Core badges ---

    public List<BadgeResponse> findAllBadges() {
        log.debug("Fetching all badges");
        return badgeRepository.findAll().stream()
                .map(this::toBadgeResponse)
                .toList();
    }

    public BadgeResponse findBadgeById(Long id) {
        log.debug("Fetching badge by id={}", id);
        Badge badge = badgeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Badge not found: id={}", id);
                    return new IllegalArgumentException("Badge not found: " + id);
                });
        return toBadgeResponse(badge);
    }

    public BadgeResponse createBadge(BadgeRequest request) {
        log.info("Creating badge: name='{}'", request.name());
        Badge badge = new Badge();
        badge.setName(request.name());
        badge.setDescription(request.description());
        BadgeResponse response = toBadgeResponse(badgeRepository.save(badge));
        log.info("Badge created: id={}, name='{}'", response.id(), response.name());
        return response;
    }

    public BadgeResponse updateBadge(Long id, BadgeRequest request) {
        log.info("Updating badge: id={}", id);
        Badge badge = badgeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Badge update failed — not found: id={}", id);
                    return new IllegalArgumentException("Badge not found: " + id);
                });
        badge.setName(request.name());
        badge.setDescription(request.description());
        return toBadgeResponse(badgeRepository.save(badge));
    }

    public void deleteBadge(Long id) {
        log.info("Deleting badge: id={}", id);
        if (!badgeRepository.existsById(id)) {
            log.warn("Badge deletion failed — not found: id={}", id);
            throw new IllegalArgumentException("Badge not found: " + id);
        }
        badgeRepository.deleteById(id);
        log.info("Badge deleted: id={}", id);
    }

    // --- Sub-badges ---

    public List<SubBadgeResponse> findSubBadgesByBadge(Long badgeId) {
        log.debug("Fetching sub-badges for badgeId={}", badgeId);
        return subBadgeRepository.findByBadgeId(badgeId).stream()
                .map(this::toSubBadgeResponse)
                .toList();
    }

    public List<SubBadgeResponse> findSubBadgesByModule(Long moduleId) {
        log.debug("Fetching sub-badges for moduleId={}", moduleId);
        return subBadgeRepository.findByModuleId(moduleId).stream()
                .map(this::toSubBadgeResponse)
                .toList();
    }

    public SubBadgeResponse findSubBadgeById(Long id) {
        log.debug("Fetching sub-badge by id={}", id);
        SubBadge sub = subBadgeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("SubBadge not found: id={}", id);
                    return new IllegalArgumentException("SubBadge not found: " + id);
                });
        return toSubBadgeResponse(sub);
    }

    public SubBadgeResponse createSubBadge(SubBadgeRequest request) {
        log.info("Creating sub-badge: name='{}', badgeId={}, moduleId={}", request.name(), request.badgeId(), request.moduleId());
        Badge badge = badgeRepository.findById(request.badgeId())
                .orElseThrow(() -> new IllegalArgumentException("Badge not found: " + request.badgeId()));
        Module module = moduleRepository.findById(request.moduleId())
                .orElseThrow(() -> new IllegalArgumentException("Module not found: " + request.moduleId()));

        SubBadge sub = new SubBadge();
        sub.setName(request.name());
        sub.setPoints(request.points());
        sub.setBadge(badge);
        sub.setModule(module);
        SubBadgeResponse response = toSubBadgeResponse(subBadgeRepository.save(sub));
        log.info("SubBadge created: id={}, name='{}', points={}", response.id(), response.name(), response.points());
        return response;
    }

    public SubBadgeResponse updateSubBadge(Long id, SubBadgeRequest request) {
        log.info("Updating sub-badge: id={}", id);
        SubBadge sub = subBadgeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("SubBadge update failed — not found: id={}", id);
                    return new IllegalArgumentException("SubBadge not found: " + id);
                });

        Badge badge = badgeRepository.findById(request.badgeId())
                .orElseThrow(() -> new IllegalArgumentException("Badge not found: " + request.badgeId()));
        Module module = moduleRepository.findById(request.moduleId())
                .orElseThrow(() -> new IllegalArgumentException("Module not found: " + request.moduleId()));

        sub.setName(request.name());
        sub.setPoints(request.points());
        sub.setBadge(badge);
        sub.setModule(module);
        return toSubBadgeResponse(subBadgeRepository.save(sub));
    }

    public void deleteSubBadge(Long id) {
        log.info("Deleting sub-badge: id={}", id);
        if (!subBadgeRepository.existsById(id)) {
            log.warn("SubBadge deletion failed — not found: id={}", id);
            throw new IllegalArgumentException("SubBadge not found: " + id);
        }
        subBadgeRepository.deleteById(id);
        log.info("SubBadge deleted: id={}", id);
    }

    // --- Mappers ---

    private BadgeResponse toBadgeResponse(Badge badge) {
        return new BadgeResponse(badge.getId(), badge.getName(), badge.getDescription(), badge.getCreatedAt());
    }

    private SubBadgeResponse toSubBadgeResponse(SubBadge sub) {
        return new SubBadgeResponse(
                sub.getId(),
                sub.getName(),
                sub.getPoints(),
                sub.getBadge().getId(),
                sub.getBadge().getName(),
                sub.getModule().getId(),
                sub.getModule().getName(),
                sub.getCreatedAt()
        );
    }
}
