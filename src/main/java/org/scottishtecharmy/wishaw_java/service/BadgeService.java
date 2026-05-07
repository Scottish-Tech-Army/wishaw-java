package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.BadgeDTO;
import org.scottishtecharmy.wishaw_java.dto.PlayerBadgeDTO;
import com.ltc.entity.*;
import com.ltc.exception.*;
import com.ltc.repository.*;
import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw_java.entity.Badge;
import org.scottishtecharmy.wishaw_java.entity.PlayerBadge;
import org.scottishtecharmy.wishaw_java.entity.Tournament;
import org.scottishtecharmy.wishaw_java.entity.User;
import org.scottishtecharmy.wishaw_java.exception.DuplicateResourceException;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.repository.BadgeRepository;
import org.scottishtecharmy.wishaw_java.repository.PlayerBadgeRepository;
import org.scottishtecharmy.wishaw_java.repository.TournamentRepository;
import org.scottishtecharmy.wishaw_java.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final PlayerBadgeRepository playerBadgeRepository;
    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;

    public List<BadgeDTO> getAllBadges() {
        return badgeRepository.findAll().stream().map(this::mapBadgeToDTO).collect(Collectors.toList());
    }

    public BadgeDTO getBadgeById(Long id) {
        Badge badge = badgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Badge", id));
        return mapBadgeToDTO(badge);
    }

    @Transactional
    public BadgeDTO createBadge(BadgeDTO dto) {
        if (badgeRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Badge with name already exists: " + dto.getName());
        }
        Badge badge = Badge.builder()
                .name(dto.getName()).description(dto.getDescription())
                .iconUrl(dto.getIconUrl()).criteria(dto.getCriteria()).build();
        Badge saved = badgeRepository.save(badge);
        return mapBadgeToDTO(saved);
    }

    @Transactional
    public BadgeDTO updateBadge(Long id, BadgeDTO dto) {
        Badge badge = badgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Badge", id));
        if (dto.getName() != null) badge.setName(dto.getName());
        if (dto.getDescription() != null) badge.setDescription(dto.getDescription());
        if (dto.getIconUrl() != null) badge.setIconUrl(dto.getIconUrl());
        if (dto.getCriteria() != null) badge.setCriteria(dto.getCriteria());
        Badge saved = badgeRepository.save(badge);
        return mapBadgeToDTO(saved);
    }

    @Transactional
    public void deleteBadge(Long id) {
        if (!badgeRepository.existsById(id)) throw new ResourceNotFoundException("Badge", id);
        badgeRepository.deleteById(id);
    }

    public List<PlayerBadgeDTO> getBadgesByPlayer(Long playerId) {
        return playerBadgeRepository.findByPlayerId(playerId).stream()
                .map(this::mapPlayerBadgeToDTO).collect(Collectors.toList());
    }

    public List<PlayerBadgeDTO> getBadgesByTournament(Long tournamentId) {
        return playerBadgeRepository.findByTournamentId(tournamentId).stream()
                .map(this::mapPlayerBadgeToDTO).collect(Collectors.toList());
    }

    @Transactional
    public PlayerBadgeDTO awardBadge(PlayerBadgeDTO dto, Long adminId) {
        User player = userRepository.findById(dto.getPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Player", dto.getPlayerId()));
        Badge badge = badgeRepository.findById(dto.getBadgeId())
                .orElseThrow(() -> new ResourceNotFoundException("Badge", dto.getBadgeId()));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));

        Long tournamentId = dto.getTournamentId();
        Tournament tournament = null;
        if (tournamentId != null) {
            tournament = tournamentRepository.findById(tournamentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Tournament", tournamentId));
        }

        if (playerBadgeRepository.existsByPlayerIdAndBadgeIdAndTournamentId(
                player.getId(), badge.getId(), tournamentId)) {
            throw new DuplicateResourceException("Player already has this badge for this tournament");
        }

        PlayerBadge playerBadge = PlayerBadge.builder()
                .player(player).badge(badge).tournament(tournament).awardedBy(admin).build();
        PlayerBadge saved = playerBadgeRepository.save(playerBadge);
        return mapPlayerBadgeToDTO(saved);
    }

    @Transactional
    public void revokeBadge(Long playerBadgeId) {
        if (!playerBadgeRepository.existsById(playerBadgeId))
            throw new ResourceNotFoundException("PlayerBadge", playerBadgeId);
        playerBadgeRepository.deleteById(playerBadgeId);
    }

    private BadgeDTO mapBadgeToDTO(Badge b) {
        return BadgeDTO.builder()
                .id(b.getId()).name(b.getName()).description(b.getDescription())
                .iconUrl(b.getIconUrl()).criteria(b.getCriteria())
                .createdAt(b.getCreatedAt() != null ? b.getCreatedAt().toString() : null).build();
    }

    private PlayerBadgeDTO mapPlayerBadgeToDTO(PlayerBadge pb) {
        return PlayerBadgeDTO.builder()
                .id(pb.getId())
                .playerId(pb.getPlayer().getId()).playerName(pb.getPlayer().getFullName())
                .badgeId(pb.getBadge().getId()).badgeName(pb.getBadge().getName())
                .badgeDescription(pb.getBadge().getDescription()).badgeIconUrl(pb.getBadge().getIconUrl())
                .tournamentId(pb.getTournament() != null ? pb.getTournament().getId() : null)
                .tournamentName(pb.getTournament() != null ? pb.getTournament().getName() : null)
                .awardedById(pb.getAwardedBy() != null ? pb.getAwardedBy().getId() : null)
                .awardedByName(pb.getAwardedBy() != null ? pb.getAwardedBy().getFullName() : null)
                .awardedAt(pb.getAwardedAt() != null ? pb.getAwardedAt().toString() : null).build();
    }
}

