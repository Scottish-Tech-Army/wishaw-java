package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.TeamDTO;
import org.scottishtecharmy.wishaw_java.dto.UserResponseDTO;
import com.ltc.entity.*;
import com.ltc.exception.*;
import com.ltc.repository.*;
import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw_java.entity.Team;
import org.scottishtecharmy.wishaw_java.entity.Tournament;
import org.scottishtecharmy.wishaw_java.entity.User;
import org.scottishtecharmy.wishaw_java.exception.DuplicateResourceException;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.repository.TeamRepository;
import org.scottishtecharmy.wishaw_java.repository.TournamentRepository;
import org.scottishtecharmy.wishaw_java.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;

    public List<TeamDTO> getTeamsByTournament(Long tournamentId) {
        return teamRepository.findByTournamentId(tournamentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TeamDTO getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", id));
        return mapToDTO(team);
    }

    public List<TeamDTO> getTeamsByCaptain(Long captainId) {
        return teamRepository.findByCaptainId(captainId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TeamDTO createTeam(TeamDTO dto) {
        Tournament tournament = tournamentRepository.findById(dto.getTournamentId())
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", dto.getTournamentId()));

        if (teamRepository.existsByNameAndTournamentId(dto.getName(), dto.getTournamentId())) {
            throw new DuplicateResourceException("Team name already exists in this tournament: " + dto.getName());
        }

        Team team = Team.builder()
                .name(dto.getName())
                .tournament(tournament)
                .organization(dto.getOrganization())
                .members(new ArrayList<>())
                .build();

        if (dto.getCaptainId() != null) {
            User captain = userRepository.findById(dto.getCaptainId())
                    .orElseThrow(() -> new ResourceNotFoundException("Captain user", dto.getCaptainId()));
            team.setCaptain(captain);
            team.getMembers().add(captain);
        }

        if (dto.getMemberIds() != null) {
            for (Long memberId : dto.getMemberIds()) {
                User member = userRepository.findById(memberId)
                        .orElseThrow(() -> new ResourceNotFoundException("User", memberId));
                if (!team.getMembers().contains(member)) {
                    team.getMembers().add(member);
                }
            }
        }

        Team saved = teamRepository.save(team);
        return mapToDTO(saved);
    }

    @Transactional
    public TeamDTO updateTeam(Long id, TeamDTO dto) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", id));

        if (dto.getName() != null) team.setName(dto.getName());
        if (dto.getOrganization() != null) team.setOrganization(dto.getOrganization());
        if (dto.getCaptainId() != null) {
            User captain = userRepository.findById(dto.getCaptainId())
                    .orElseThrow(() -> new ResourceNotFoundException("Captain user", dto.getCaptainId()));
            team.setCaptain(captain);
        }

        Team saved = teamRepository.save(team);
        return mapToDTO(saved);
    }

    @Transactional
    public TeamDTO addMember(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", teamId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (team.getMembers().stream().anyMatch(m -> m.getId().equals(userId))) {
            throw new DuplicateResourceException("User is already a member of this team");
        }

        team.getMembers().add(user);
        Team saved = teamRepository.save(team);
        return mapToDTO(saved);
    }

    @Transactional
    public TeamDTO removeMember(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", teamId));

        team.getMembers().removeIf(m -> m.getId().equals(userId));
        Team saved = teamRepository.save(team);
        return mapToDTO(saved);
    }

    @Transactional
    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Team", id);
        }
        teamRepository.deleteById(id);
    }

    private TeamDTO mapToDTO(Team team) {
        List<UserResponseDTO> memberDTOs = team.getMembers().stream()
                .map(u -> UserResponseDTO.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .fullName(u.getFullName())
                        .email(u.getEmail())
                        .role(u.getRole().name())
                        .organization(u.getOrganization())
                        .build())
                .collect(Collectors.toList());

        return TeamDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .tournamentId(team.getTournament().getId())
                .tournamentName(team.getTournament().getName())
                .captainId(team.getCaptain() != null ? team.getCaptain().getId() : null)
                .captainName(team.getCaptain() != null ? team.getCaptain().getFullName() : null)
                .memberIds(team.getMembers().stream().map(User::getId).collect(Collectors.toList()))
                .members(memberDTOs)
                .organization(team.getOrganization())
                .createdAt(team.getCreatedAt() != null ? team.getCreatedAt().toString() : null)
                .build();
    }
}

