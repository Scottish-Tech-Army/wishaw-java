package org.scottishtecharmy.wishaw_java.service.player;

import org.scottishtecharmy.wishaw_java.dto.request.AwardChallengeRequest;
import org.scottishtecharmy.wishaw_java.dto.request.SetLegacyPointsRequest;
import org.scottishtecharmy.wishaw_java.dto.response.BadgeProgressResponse;
import org.scottishtecharmy.wishaw_java.dto.response.PlayerProfileResponse;
import org.scottishtecharmy.wishaw_java.entity.BadgeCategory;
import org.scottishtecharmy.wishaw_java.entity.BadgeLevel;
import org.scottishtecharmy.wishaw_java.entity.Challenge;
import org.scottishtecharmy.wishaw_java.entity.ChallengeAward;
import org.scottishtecharmy.wishaw_java.entity.PlayerBadgeProgress;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.enums.Role;
import org.scottishtecharmy.wishaw_java.enums.SourceType;
import org.scottishtecharmy.wishaw_java.exception.BadRequestException;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.mapper.DtoMapper;
import org.scottishtecharmy.wishaw_java.repository.BadgeCategoryRepository;
import org.scottishtecharmy.wishaw_java.repository.BadgeLevelRepository;
import org.scottishtecharmy.wishaw_java.repository.ChallengeAwardRepository;
import org.scottishtecharmy.wishaw_java.repository.ChallengeRepository;
import org.scottishtecharmy.wishaw_java.repository.PlayerBadgeProgressRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgressService {

    private final UserAccountRepository userAccountRepository;
    private final BadgeCategoryRepository badgeCategoryRepository;
    private final BadgeLevelRepository badgeLevelRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeAwardRepository challengeAwardRepository;
    private final PlayerBadgeProgressRepository playerBadgeProgressRepository;

    public BadgeProgressResponse setLegacyPoints(SetLegacyPointsRequest request) {
        return setLegacyPoints(request, null);
    }

    public BadgeProgressResponse setLegacyPoints(SetLegacyPointsRequest request, String updatedByUsername) {
        UserAccount player = getPlayer(request.getPlayerId());
        assertCanManagePlayer(getOptionalUser(updatedByUsername), player);
        BadgeCategory badgeCategory = getBadgeCategory(request.getBadgeCategoryId());
        PlayerBadgeProgress progress = getOrCreateProgress(player, badgeCategory);
        progress.setLegacyPoints(request.getLegacyPoints());
        playerBadgeProgressRepository.save(progress);
        recalculatePlayerProgress(player.getId());
        return DtoMapper.toBadgeProgress(getOrCreateProgress(player, badgeCategory));
    }

    public BadgeProgressResponse awardChallenge(AwardChallengeRequest request, String awardedByUsername) {
        UserAccount player = getPlayer(request.getPlayerId());
        Challenge challenge = challengeRepository.findById(request.getChallengeId())
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found: " + request.getChallengeId()));
        UserAccount awardedBy = getRequiredUser(awardedByUsername);
        assertCanManagePlayer(awardedBy, player);

        ChallengeAward award = new ChallengeAward();
        award.setPlayer(player);
        award.setModule(challenge.getModule());
        award.setChallenge(challenge);
        award.setBadgeCategory(challenge.getBadgeCategory());
        award.setAwardedPoints(challenge.getPoints());
        award.setAwardedBy(awardedBy);
        award.setSourceType(SourceType.MANUAL_ADMIN);
        award.setSourceReference("manual-" + player.getId() + "-" + challenge.getId() + "-" + System.currentTimeMillis());
        award.setAwardDate(LocalDate.now());
        award.setNotes(request.getNotes());
        challengeAwardRepository.save(award);

        recalculatePlayerProgress(player.getId());
        return DtoMapper.toBadgeProgress(getOrCreateProgress(player, challenge.getBadgeCategory()));
    }

    public PlayerProfileResponse getPlayerProfile(Long playerId) {
        return getPlayerProfile(playerId, null);
    }

    public PlayerProfileResponse getPlayerProfile(Long playerId, String requestingUsername) {
        UserAccount player = getPlayer(playerId);
        assertCanManagePlayer(getOptionalUser(requestingUsername), player);
        initializeProgressEntries(player);
        return DtoMapper.toPlayerProfile(player, playerBadgeProgressRepository.findByPlayerId(playerId));
    }

    public List<BadgeProgressResponse> getPlayerProgress(Long playerId) {
        return getPlayerProgress(playerId, null);
    }

    public List<BadgeProgressResponse> getPlayerProgress(Long playerId, String requestingUsername) {
        UserAccount player = getPlayer(playerId);
        assertCanManagePlayer(getOptionalUser(requestingUsername), player);
        initializeProgressEntries(player);
        return playerBadgeProgressRepository.findByPlayerId(playerId).stream()
                .sorted(Comparator.comparing(progress -> progress.getBadgeCategory().getDisplayName()))
                .map(DtoMapper::toBadgeProgress)
                .toList();
    }

    public PlayerProfileResponse getCurrentPlayerProfile(String username) {
        UserAccount player = getPlayerByUsername(username);
        return getPlayerProfile(player.getId());
    }

    public List<BadgeProgressResponse> getCurrentPlayerProgress(String username) {
        UserAccount player = getPlayerByUsername(username);
        return getPlayerProgress(player.getId());
    }

    public void recalculateAllPlayerProgress() {
        userAccountRepository.findByRole(Role.PLAYER).forEach(player -> recalculatePlayerProgress(player.getId()));
    }

    public void recalculatePlayerProgress(Long playerId) {
        UserAccount player = getPlayer(playerId);
        List<BadgeCategory> categories = badgeCategoryRepository.findAll().stream()
                .filter(BadgeCategory::isActive)
                .toList();

        for (BadgeCategory category : categories) {
            PlayerBadgeProgress progress = getOrCreateProgress(player, category);
            int earnedPoints = challengeAwardRepository.sumPointsByPlayerAndCategory(playerId, category.getId());
            progress.setEarnedPoints(earnedPoints);
            progress.setCurrentLevelName(resolveLevelName(progress.getLegacyPoints() + earnedPoints));
            playerBadgeProgressRepository.save(progress);
        }
    }

    private void initializeProgressEntries(UserAccount player) {
        badgeCategoryRepository.findAll().stream()
                .filter(BadgeCategory::isActive)
                .forEach(category -> getOrCreateProgress(player, category));
    }

    private PlayerBadgeProgress getOrCreateProgress(UserAccount player, BadgeCategory category) {
        return playerBadgeProgressRepository.findByPlayerIdAndBadgeCategoryId(player.getId(), category.getId())
                .orElseGet(() -> createProgress(player, category));
    }

    private PlayerBadgeProgress createProgress(UserAccount player, BadgeCategory category) {
        PlayerBadgeProgress progress = new PlayerBadgeProgress();
        progress.setPlayer(player);
        progress.setBadgeCategory(category);
        progress.setCurrentLevelName(resolveLevelName(0));

        try {
            return playerBadgeProgressRepository.saveAndFlush(progress);
        } catch (DataIntegrityViolationException ex) {
            return playerBadgeProgressRepository.findByPlayerIdAndBadgeCategoryId(player.getId(), category.getId())
                    .orElseThrow(() -> ex);
        }
    }

    private String resolveLevelName(int totalPoints) {
        return badgeLevelRepository.findAllByActiveTrueOrderByRankOrderAsc().stream()
                .filter(level -> isInRange(level, totalPoints))
                .map(BadgeLevel::getName)
                .findFirst()
                .orElse(null);
    }

    private boolean isInRange(BadgeLevel level, int totalPoints) {
        boolean minMatch = totalPoints >= level.getMinPoints();
        boolean maxMatch = level.getMaxPoints() == null || totalPoints <= level.getMaxPoints();
        return minMatch && maxMatch;
    }

    private UserAccount getPlayer(Long playerId) {
        UserAccount player = userAccountRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found: " + playerId));
        if (player.getRole() != Role.PLAYER) {
            throw new BadRequestException("User is not a player");
        }
        return player;
    }

    private UserAccount getPlayerByUsername(String username) {
        UserAccount player = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found: " + username));
        if (player.getRole() != Role.PLAYER) {
            throw new BadRequestException("Authenticated user is not a player");
        }
        return player;
    }

    private BadgeCategory getBadgeCategory(Long badgeCategoryId) {
        return badgeCategoryRepository.findById(badgeCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Badge category not found: " + badgeCategoryId));
    }

    private UserAccount getOptionalUser(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return getRequiredUser(username);
    }

    private UserAccount getRequiredUser(String username) {
        return userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private void assertCanManagePlayer(UserAccount actor, UserAccount player) {
        if (actor == null || actor.getRole() == Role.SUPER_ADMIN) {
            return;
        }
        if (actor.getRole() != Role.CENTRE_ADMIN) {
            throw new AccessDeniedException("User is not allowed to manage player progress");
        }

        Long actorCentreId = actor.getCentre() != null ? actor.getCentre().getId() : null;
        Long playerCentreId = player.getCentre() != null ? player.getCentre().getId() : null;
        if (actorCentreId == null || !actorCentreId.equals(playerCentreId)) {
            throw new AccessDeniedException("Centre admin cannot access players outside their centre");
        }
    }
}
