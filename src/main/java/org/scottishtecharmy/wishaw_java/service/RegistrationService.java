package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.RegistrationDTO;
import com.ltc.entity.*;
import org.scottishtecharmy.wishaw_java.entity.Team;
import org.scottishtecharmy.wishaw_java.entity.Tournament;
import org.scottishtecharmy.wishaw_java.entity.TournamentRegistration;
import org.scottishtecharmy.wishaw_java.entity.User;
import org.scottishtecharmy.wishaw_java.enums.TournamentStatus;
import com.ltc.exception.*;
import com.ltc.repository.*;
import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw_java.exception.BadRequestException;
import org.scottishtecharmy.wishaw_java.exception.DuplicateResourceException;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.repository.TeamRepository;
import org.scottishtecharmy.wishaw_java.repository.TournamentRegistrationRepository;
import org.scottishtecharmy.wishaw_java.repository.TournamentRepository;
import org.scottishtecharmy.wishaw_java.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final TournamentRegistrationRepository registrationRepository;
    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    public List<RegistrationDTO> getRegistrationsByTournament(Long tournamentId) {
        return registrationRepository.findByTournamentId(tournamentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<RegistrationDTO> getRegistrationsByPlayer(Long playerId) {
        return registrationRepository.findByPlayerId(playerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RegistrationDTO registerPlayer(RegistrationDTO dto) {
        Tournament tournament = tournamentRepository.findById(dto.getTournamentId())
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", dto.getTournamentId()));

        User player = userRepository.findById(dto.getPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Player", dto.getPlayerId()));

        if (tournament.getStatus() != TournamentStatus.REGISTRATION_OPEN
                && tournament.getStatus() != TournamentStatus.UPCOMING) {
            throw new BadRequestException("Tournament registration is not open");
        }

        LocalDateTime now = LocalDateTime.now();
        if (tournament.getRegistrationStartDate() != null && now.isBefore(tournament.getRegistrationStartDate())) {
            throw new BadRequestException("Registration has not started yet");
        }
        if (tournament.getRegistrationEndDate() != null && now.isAfter(tournament.getRegistrationEndDate())) {
            throw new BadRequestException("Registration period has ended");
        }

        if (tournament.getMaxParticipants() != null) {
            long currentCount = registrationRepository.countByTournamentId(tournament.getId());
            if (currentCount >= tournament.getMaxParticipants()) {
                throw new BadRequestException("Tournament has reached maximum capacity");
            }
        }

        if (registrationRepository.existsByTournamentIdAndPlayerId(tournament.getId(), player.getId())) {
            throw new DuplicateResourceException("Player is already registered for this tournament");
        }

        TournamentRegistration registration = TournamentRegistration.builder()
                .tournament(tournament)
                .player(player)
                .build();

        if (dto.getTeamId() != null) {
            Team team = teamRepository.findById(dto.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team", dto.getTeamId()));
            registration.setTeam(team);
        }

        TournamentRegistration saved = registrationRepository.save(registration);
        return mapToDTO(saved);
    }

    @Transactional
    public void unregisterPlayer(Long tournamentId, Long playerId) {
        TournamentRegistration registration = registrationRepository
                .findByTournamentIdAndPlayerId(tournamentId, playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        registrationRepository.delete(registration);
    }

    private RegistrationDTO mapToDTO(TournamentRegistration reg) {
        return RegistrationDTO.builder()
                .id(reg.getId())
                .tournamentId(reg.getTournament().getId())
                .tournamentName(reg.getTournament().getName())
                .playerId(reg.getPlayer().getId())
                .playerName(reg.getPlayer().getFullName())
                .teamId(reg.getTeam() != null ? reg.getTeam().getId() : null)
                .teamName(reg.getTeam() != null ? reg.getTeam().getName() : null)
                .registeredAt(reg.getRegisteredAt() != null ? reg.getRegisteredAt().toString() : null)
                .build();
    }
}

