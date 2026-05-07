package org.scottishtecharmy.wishaw_java.controller;

import com.ltc.dto.*;
import org.scottishtecharmy.wishaw_java.dto.ApiResponse;
import org.scottishtecharmy.wishaw_java.dto.AttendanceDTO;
import org.scottishtecharmy.wishaw_java.enums.AttendanceStatus;
import org.scottishtecharmy.wishaw_java.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Match attendance marking and tracking APIs")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/match/{matchId}")
    @Operation(summary = "Get attendance by match")
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getByMatch(@PathVariable Long matchId) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getAttendanceByMatch(matchId)));
    }

    @GetMapping("/player/{playerId}")
    @Operation(summary = "Get attendance by player")
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getByPlayer(@PathVariable Long playerId) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getAttendanceByPlayer(playerId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark attendance", description = "Admin: Mark attendance for a player in a match")
    public ResponseEntity<ApiResponse<AttendanceDTO>> markAttendance(
            @Valid @RequestBody AttendanceDTO dto, @RequestParam Long adminId) {
        AttendanceDTO result = attendanceService.markAttendance(dto, adminId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Attendance marked successfully", result));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark bulk attendance", description = "Admin: Mark attendance for multiple players at once")
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> markBulkAttendance(
            @Valid @RequestBody List<AttendanceDTO> dtos, @RequestParam Long adminId) {
        List<AttendanceDTO> results = attendanceService.markBulkAttendance(dtos, adminId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bulk attendance marked successfully", results));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update attendance", description = "Admin: Update attendance status")
    public ResponseEntity<ApiResponse<AttendanceDTO>> updateAttendance(
            @PathVariable Long id,
            @RequestParam AttendanceStatus status,
            @RequestParam Long adminId) {
        return ResponseEntity.ok(ApiResponse.success("Attendance updated successfully",
                attendanceService.updateAttendance(id, status, adminId)));
    }
}

