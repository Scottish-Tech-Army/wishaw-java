package org.scottishtecharmy.wishaw_java.level;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw_java.level.dto.LevelRequest;
import org.scottishtecharmy.wishaw_java.level.dto.LevelResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/levels")
@RequiredArgsConstructor
@Tag(name = "Levels", description = "Level threshold management")
public class LevelController {

    private final LevelService levelService;

    @GetMapping
    @Operation(summary = "List all levels (ordered by displayOrder)")
    public ResponseEntity<List<LevelResponse>> getAll() {
        return ResponseEntity.ok(levelService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get level by ID")
    public ResponseEntity<LevelResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(levelService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @Operation(summary = "Create a new level (MAIN_ADMIN only)")
    public ResponseEntity<LevelResponse> create(@Valid @RequestBody LevelRequest request) {
        return ResponseEntity.ok(levelService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @Operation(summary = "Update a level (MAIN_ADMIN only)")
    public ResponseEntity<LevelResponse> update(@PathVariable Long id, @Valid @RequestBody LevelRequest request) {
        return ResponseEntity.ok(levelService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @Operation(summary = "Delete a level (MAIN_ADMIN only)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        levelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

