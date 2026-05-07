package org.scottishtecharmy.wishaw.service;

import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw.entity.*;
import org.scottishtecharmy.wishaw.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerBadgeDetailRepository playerBadgeDetailRepository;
    private final PlayerLevelDetailRepository playerLevelDetailRepository;
    private final LegacyPointRepository legacyPointRepository;
    private final LevelRepository levelRepository;
    private final BadgeRepository badgeRepository;

    @Transactional(readOnly = true)
    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Player> findByCentre(Centre centre) {
        return playerRepository.findByCentre(centre);
    }

    @Transactional(readOnly = true)
    public List<Player> findByCentreLeaderboard(Centre centre) {
        return playerRepository.findByCentreOrderByTotalXpDesc(centre);
    }

    @Transactional(readOnly = true)
    public Player findById(Long id) {
        return playerRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Player findByUser(User user) {
        return playerRepository.findByUser(user).orElse(null);
    }

    @Transactional
    public Player save(Player player) {
        return playerRepository.save(player);
    }

    @Transactional
    public void delete(Long id) {
        playerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PlayerBadgeDetail> findPlayerBadges(Player player) {
        return playerBadgeDetailRepository.findByPlayer(player);
    }

    @Transactional(readOnly = true)
    public List<PlayerBadgeDetail> findPendingApprovals(Player player) {
        return playerBadgeDetailRepository.findByPlayerAndApproved(player, false);
    }

    @Transactional
    public void submitEvidence(Player player, Badge badge, SubBadge subBadge, byte[] evidenceData, String evidenceUrl) {
        if (!playerBadgeDetailRepository.existsByPlayerAndSubBadgeId(player, subBadge.getId())) {
            PlayerBadgeDetail detail = new PlayerBadgeDetail();
            detail.setPlayer(player);
            detail.setBadge(badge);
            detail.setSubBadge(subBadge);
            detail.setEvidenceData(evidenceData);
            detail.setEvidenceUrl(evidenceUrl);
            detail.setApproved(false);
            playerBadgeDetailRepository.save(detail);
        }
    }

    @Transactional
    public void approveChallenge(PlayerBadgeDetail detail, String coachNotes) {
        detail.setApproved(true);
        detail.setEarnedDate(LocalDate.now());
        detail.setCoachNotes(coachNotes);
        playerBadgeDetailRepository.save(detail);
        recalculatePlayerXp(detail.getPlayer());
    }

    @Transactional
    public void rejectChallenge(PlayerBadgeDetail detail, String coachNotes) {
        detail.setCoachNotes(coachNotes);
        playerBadgeDetailRepository.delete(detail);
    }

    @Transactional
    public void recalculatePlayerXp(Player player) {
        List<PlayerBadgeDetail> approved = playerBadgeDetailRepository.findByPlayerAndApproved(player, true);
        int totalXp = approved.stream()
                .mapToInt(d -> d.getSubBadge().getPoint())
                .sum();

        // Add legacy points
        List<LegacyPoint> legacyPoints = legacyPointRepository.findByPlayer(player);
        totalXp += legacyPoints.stream().mapToInt(LegacyPoint::getLegacyPointValue).sum();

        player.setTotalXp(totalXp);
        playerRepository.save(player);

        // Update badge-level XP
        List<Badge> allBadges = badgeRepository.findAll();
        for (Badge badge : allBadges) {
            int badgeXp = approved.stream()
                    .filter(d -> d.getBadge().getId().equals(badge.getId()))
                    .mapToInt(d -> d.getSubBadge().getPoint())
                    .sum();
            // Add legacy points for this badge
            badgeXp += legacyPoints.stream()
                    .filter(lp -> lp.getBadge().getId().equals(badge.getId()))
                    .mapToInt(LegacyPoint::getLegacyPointValue)
                    .sum();

            if (badgeXp > 0) {
                Optional<PlayerLevelDetail> pldOpt = playerLevelDetailRepository.findByPlayerAndBadge(player, badge);
                Level level = levelRepository.findTopByMinPointsLessThanEqualOrderByMinPointsDesc(badgeXp);
                PlayerLevelDetail pld = pldOpt.orElse(new PlayerLevelDetail());
                pld.setPlayer(player);
                pld.setBadge(badge);
                pld.setBadgeXp(badgeXp);
                if (level != null) {
                    pld.setLevel(level);
                }
                playerLevelDetailRepository.save(pld);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<PlayerLevelDetail> findPlayerLevels(Player player) {
        return playerLevelDetailRepository.findByPlayer(player);
    }

    @Transactional(readOnly = true)
    public List<LegacyPoint> findLegacyPoints(Player player) {
        return legacyPointRepository.findByPlayer(player);
    }

    @Transactional
    public void addLegacyPoint(LegacyPoint legacyPoint) {
        legacyPointRepository.save(legacyPoint);
        recalculatePlayerXp(legacyPoint.getPlayer());
    }
}
