package org.scottishtecharmy.wishaw_java.service.importer;

import org.scottishtecharmy.wishaw_java.dto.response.ImportCommitResponse;
import org.scottishtecharmy.wishaw_java.entity.BadgeCategory;
import org.scottishtecharmy.wishaw_java.entity.ChallengeAward;
import org.scottishtecharmy.wishaw_java.entity.ImportBatch;
import org.scottishtecharmy.wishaw_java.entity.PlayerBadgeProgress;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.enums.Role;
import org.scottishtecharmy.wishaw_java.enums.ImportStatus;
import org.scottishtecharmy.wishaw_java.enums.SourceType;
import org.scottishtecharmy.wishaw_java.exception.BadRequestException;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.repository.BadgeCategoryRepository;
import org.scottishtecharmy.wishaw_java.repository.ChallengeAwardRepository;
import org.scottishtecharmy.wishaw_java.repository.ImportBatchRepository;
import org.scottishtecharmy.wishaw_java.repository.PlayerBadgeProgressRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import org.scottishtecharmy.wishaw_java.service.player.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ImportCommitService {
    private final ImportBatchRepository importBatchRepository;
    private final BadgeCategoryRepository badgeCategoryRepository;
    private final UserAccountRepository userAccountRepository;
    private final ChallengeAwardRepository challengeAwardRepository;
    private final PlayerBadgeProgressRepository playerBadgeProgressRepository;
    private final ImportPreviewService importPreviewService;
    private final ProgressService progressService;

    public ImportCommitResponse commit(Long batchId) {
        ImportBatch batch = importBatchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Import batch not found: " + batchId));

        if (batch.getStatus() == ImportStatus.COMMITTED) {
            return ImportCommitResponse.builder()
                    .batchId(batch.getId())
                    .status(batch.getStatus().name())
                    .totalAwardsCreated(0)
                    .totalPlayersAffected(0)
                    .message("Import batch was already committed")
                    .build();
        }

        ImportBatchState state = importPreviewService.loadState(batch);
        ensureAllPlayersResolved(state);
        int awardsCreated = 0;
        Set<Long> affectedPlayers = new HashSet<>();

        for (ImportRowState rowState : state.getRows()) {
            if ("ERROR".equals(rowState.getStatus())) {
                continue;
            }

            Map<String, String> row = rowState.getData();
            String username = getValue(row, "username", "playerUsername", "player");
            String badgeCategoryCode = getValue(row, "badgeCategoryCode", "category", "badge");
            BadgeCategory badgeCategory = badgeCategoryRepository.findByCode(badgeCategoryCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Badge category not found: " + badgeCategoryCode));

            UserAccount player = resolvePlayer(state, username);
            affectedPlayers.add(player.getId());

            Integer legacyPoints = parseInteger(getValue(row, "legacyPoints", "legacy"));
            if (legacyPoints != null && legacyPoints > 0) {
                PlayerBadgeProgress progress = playerBadgeProgressRepository
                        .findByPlayerIdAndBadgeCategoryId(player.getId(), badgeCategory.getId())
                        .orElseGet(() -> {
                            PlayerBadgeProgress created = new PlayerBadgeProgress();
                            created.setPlayer(player);
                            created.setBadgeCategory(badgeCategory);
                            created.setCurrentLevelName(null);
                            return playerBadgeProgressRepository.save(created);
                        });
                progress.setLegacyPoints(progress.getLegacyPoints() + legacyPoints);
                playerBadgeProgressRepository.save(progress);
            }

            Integer challengePoints = parseInteger(getValue(row, "challengePoints", "points", "awardPoints"));
            if (challengePoints != null && challengePoints > 0) {
                ChallengeAward award = new ChallengeAward();
                award.setPlayer(player);
                award.setBadgeCategory(badgeCategory);
                award.setAwardedPoints(challengePoints);
                award.setSourceType(SourceType.CSV_IMPORT);
                award.setSourceReference(getValue(row, "sourceReference", "source", "rowId") != null
                        ? getValue(row, "sourceReference", "source", "rowId")
                        : "batch-" + batch.getId() + "-row-" + rowState.getRowNumber());
                award.setAwardDate(LocalDate.now());
                award.setImportBatch(batch);
                award.setNotes(getValue(row, "challengeName", "notes"));
                challengeAwardRepository.save(award);
                awardsCreated++;
            }
        }

        for (Long playerId : affectedPlayers) {
            progressService.recalculatePlayerProgress(playerId);
        }

        batch.setStatus(ImportStatus.COMMITTED);
        batch.setCompletedAt(LocalDateTime.now());
        importBatchRepository.save(batch);

        return ImportCommitResponse.builder()
                .batchId(batch.getId())
                .status(batch.getStatus().name())
                .totalAwardsCreated(awardsCreated)
                .totalPlayersAffected(affectedPlayers.size())
                .message("Import committed successfully")
                .build();
    }

    private void ensureAllPlayersResolved(ImportBatchState state) {
        if (!state.getUnmappedPlayers().isEmpty()) {
            throw new BadRequestException("All import rows must be mapped to existing player accounts before commit");
        }
    }

    private UserAccount resolvePlayer(ImportBatchState state, String username) {
        UserAccount player;
        if (state.getPlayerMappings().containsKey(username)) {
            Long userId = state.getPlayerMappings().get(username);
            player = userAccountRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Mapped player not found: " + userId));
        } else {
            player = userAccountRepository.findByUsername(username)
                    .orElseThrow(() -> new BadRequestException(
                            "Import row references unknown player '" + username + "'. Map all missing players before commit"
                    ));
        }

        if (player.getRole() != Role.PLAYER) {
            throw new BadRequestException("Import rows can only target player accounts: " + username);
        }
        return player;
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Invalid numeric value in import row: " + value);
        }
    }

    private String getValue(Map<String, String> data, String... candidates) {
        for (String candidate : candidates) {
            if (data.containsKey(candidate)) {
                return data.get(candidate);
            }
            String matched = data.keySet().stream()
                    .filter(key -> key.equalsIgnoreCase(candidate))
                    .findFirst()
                    .orElse(null);
            if (matched != null) {
                return data.get(matched);
            }
        }
        return null;
    }
}
