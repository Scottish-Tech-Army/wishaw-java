package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.TournamentDTO;
import org.scottishtecharmy.wishaw_java.entity.Tournament;
import org.scottishtecharmy.wishaw_java.entity.User;
import com.ltc.enums.*;
import com.ltc.exception.*;
import org.scottishtecharmy.wishaw_java.enums.SportType;
import org.scottishtecharmy.wishaw_java.enums.TournamentStatus;
import org.scottishtecharmy.wishaw_java.enums.UserRole;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.exception.UnauthorizedException;
import org.scottishtecharmy.wishaw_java.repository.TournamentRegistrationRepository;
import org.scottishtecharmy.wishaw_java.repository.TournamentRepository;
import org.scottishtecharmy.wishaw_java.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;
    private final TournamentRegistrationRepository registrationRepository;

    public List<TournamentDTO> getAllTournaments() {
        return tournamentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TournamentDTO getTournamentById(Long id) {
        Tournament t = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id));
        return mapToDTO(t);
    }

    public List<TournamentDTO> getTournamentsByStatus(TournamentStatus status) {
        return tournamentRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TournamentDTO> getTournamentsBySport(SportType sportType) {
        return tournamentRepository.findBySportType(sportType).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TournamentDTO> getTournamentsByAdmin(Long adminId) {
        return tournamentRepository.findByCreatedById(adminId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TournamentDTO createTournament(TournamentDTO dto, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user", adminId));
        if (admin.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("Only admins can create tournaments");
        }

        Tournament tournament = Tournament.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .sportType(dto.getSportType())
                .participationType(dto.getParticipationType())
                .organizationType(dto.getOrganizationType())
                .status(dto.getStatus() != null ? dto.getStatus() : TournamentStatus.UPCOMING)
                .maxParticipants(dto.getMaxParticipants())
                .registrationStartDate(dto.getRegistrationStartDate() != null ? LocalDateTime.parse(dto.getRegistrationStartDate()) : null)
                .registrationEndDate(dto.getRegistrationEndDate() != null ? LocalDateTime.parse(dto.getRegistrationEndDate()) : null)
                .startDate(dto.getStartDate() != null ? LocalDateTime.parse(dto.getStartDate()) : null)
                .endDate(dto.getEndDate() != null ? LocalDateTime.parse(dto.getEndDate()) : null)
                .location(dto.getLocation())
                .rules(dto.getRules())
                .createdBy(admin)
                .build();

        Tournament saved = tournamentRepository.save(tournament);
        return mapToDTO(saved);
    }

    @Transactional
    public TournamentDTO updateTournament(Long id, TournamentDTO dto) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id));

        if (dto.getName() != null) tournament.setName(dto.getName());
        if (dto.getDescription() != null) tournament.setDescription(dto.getDescription());
        if (dto.getSportType() != null) tournament.setSportType(dto.getSportType());
        if (dto.getParticipationType() != null) tournament.setParticipationType(dto.getParticipationType());
        if (dto.getOrganizationType() != null) tournament.setOrganizationType(dto.getOrganizationType());
        if (dto.getStatus() != null) tournament.setStatus(dto.getStatus());
        if (dto.getMaxParticipants() != null) tournament.setMaxParticipants(dto.getMaxParticipants());
        if (dto.getRegistrationStartDate() != null) tournament.setRegistrationStartDate(LocalDateTime.parse(dto.getRegistrationStartDate()));
        if (dto.getRegistrationEndDate() != null) tournament.setRegistrationEndDate(LocalDateTime.parse(dto.getRegistrationEndDate()));
        if (dto.getStartDate() != null) tournament.setStartDate(LocalDateTime.parse(dto.getStartDate()));
        if (dto.getEndDate() != null) tournament.setEndDate(LocalDateTime.parse(dto.getEndDate()));
        if (dto.getLocation() != null) tournament.setLocation(dto.getLocation());
        if (dto.getRules() != null) tournament.setRules(dto.getRules());

        Tournament saved = tournamentRepository.save(tournament);
        return mapToDTO(saved);
    }

    @Transactional
    public void deleteTournament(Long id) {
        if (!tournamentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tournament", id);
        }
        tournamentRepository.deleteById(id);
    }

    private TournamentDTO mapToDTO(Tournament t) {
        return TournamentDTO.builder()
                .id(t.getId())
                .name(t.getName())
                .description(t.getDescription())
                .sportType(t.getSportType())
                .participationType(t.getParticipationType())
                .organizationType(t.getOrganizationType())
                .status(t.getStatus())
                .maxParticipants(t.getMaxParticipants())
                .registrationStartDate(t.getRegistrationStartDate() != null ? t.getRegistrationStartDate().toString() : null)
                .registrationEndDate(t.getRegistrationEndDate() != null ? t.getRegistrationEndDate().toString() : null)
                .startDate(t.getStartDate() != null ? t.getStartDate().toString() : null)
                .endDate(t.getEndDate() != null ? t.getEndDate().toString() : null)
                .location(t.getLocation())
                .rules(t.getRules())
                .createdById(t.getCreatedBy().getId())
                .createdByName(t.getCreatedBy().getFullName())
                .createdAt(t.getCreatedAt() != null ? t.getCreatedAt().toString() : null)
                .updatedAt(t.getUpdatedAt() != null ? t.getUpdatedAt().toString() : null)
                .registrationCount(registrationRepository.countByTournamentId(t.getId()))
                .build();
    }
}
