package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.AttendanceDTO;
import com.ltc.entity.*;
import org.scottishtecharmy.wishaw_java.entity.Attendance;
import org.scottishtecharmy.wishaw_java.entity.Match;
import org.scottishtecharmy.wishaw_java.entity.User;
import org.scottishtecharmy.wishaw_java.enums.AttendanceStatus;
import com.ltc.exception.*;
import com.ltc.repository.*;
import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.repository.AttendanceRepository;
import org.scottishtecharmy.wishaw_java.repository.MatchRepository;
import org.scottishtecharmy.wishaw_java.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    public List<AttendanceDTO> getAttendanceByMatch(Long matchId) {
        return attendanceRepository.findByMatchId(matchId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<AttendanceDTO> getAttendanceByPlayer(Long playerId) {
        return attendanceRepository.findByPlayerId(playerId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public AttendanceDTO markAttendance(AttendanceDTO dto, Long adminId) {
        Match match = matchRepository.findById(dto.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match", dto.getMatchId()));
        User player = userRepository.findById(dto.getPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Player", dto.getPlayerId()));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));

        Attendance existing = attendanceRepository.findByMatchIdAndPlayerId(dto.getMatchId(), dto.getPlayerId())
                .orElse(null);

        if (existing != null) {
            existing.setStatus(dto.getStatus());
            existing.setMarkedBy(admin);
            Attendance saved = attendanceRepository.save(existing);
            return mapToDTO(saved);
        }

        Attendance attendance = Attendance.builder()
                .match(match).player(player).status(dto.getStatus()).markedBy(admin).build();

        Attendance saved = attendanceRepository.save(attendance);
        return mapToDTO(saved);
    }

    @Transactional
    public List<AttendanceDTO> markBulkAttendance(List<AttendanceDTO> dtos, Long adminId) {
        return dtos.stream().map(dto -> markAttendance(dto, adminId)).collect(Collectors.toList());
    }

    @Transactional
    public AttendanceDTO updateAttendance(Long id, AttendanceStatus status, Long adminId) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance", id));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));

        attendance.setStatus(status);
        attendance.setMarkedBy(admin);
        Attendance saved = attendanceRepository.save(attendance);
        return mapToDTO(saved);
    }

    private AttendanceDTO mapToDTO(Attendance a) {
        return AttendanceDTO.builder()
                .id(a.getId())
                .matchId(a.getMatch().getId())
                .matchTitle(a.getMatch().getMatchTitle())
                .playerId(a.getPlayer().getId())
                .playerName(a.getPlayer().getFullName())
                .status(a.getStatus())
                .markedById(a.getMarkedBy() != null ? a.getMarkedBy().getId() : null)
                .markedByName(a.getMarkedBy() != null ? a.getMarkedBy().getFullName() : null)
                .createdAt(a.getCreatedAt() != null ? a.getCreatedAt().toString() : null)
                .updatedAt(a.getUpdatedAt() != null ? a.getUpdatedAt().toString() : null)
                .build();
    }
}

