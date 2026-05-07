package org.scottishtecharmy.wishaw_java.module;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw_java.module.dto.ModuleRequest;
import org.scottishtecharmy.wishaw_java.module.dto.ModuleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
@Tag(name = "Modules", description = "Game module management (e.g. Minecraft, Rocket League, Fortnite)")
public class ModuleController {

    private final ModuleService moduleService;

    @GetMapping
    @Operation(summary = "List all modules")
    public ResponseEntity<List<ModuleResponse>> getAll() {
        return ResponseEntity.ok(moduleService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get module by ID")
    public ResponseEntity<ModuleResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(moduleService.findById(id));
    }

    @GetMapping("/centre/{centreId}")
    @Operation(summary = "List modules for a centre")
    public ResponseEntity<List<ModuleResponse>> getByCentre(@PathVariable Long centreId) {
        return ResponseEntity.ok(moduleService.findByCentre(centreId));
    }

    @GetMapping("/approved")
    @Operation(summary = "List all approved modules")
    public ResponseEntity<List<ModuleResponse>> getApproved() {
        return ResponseEntity.ok(moduleService.findApproved());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'CENTRE_ADMIN')")
    @Operation(summary = "Create a new module (admin only)")
    public ResponseEntity<ModuleResponse> create(@Valid @RequestBody ModuleRequest request) {
        return ResponseEntity.ok(moduleService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'CENTRE_ADMIN')")
    @Operation(summary = "Update a module (admin only)")
    public ResponseEntity<ModuleResponse> update(@PathVariable Long id, @Valid @RequestBody ModuleRequest request) {
        return ResponseEntity.ok(moduleService.update(id, request));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @Operation(summary = "Approve a module (MAIN_ADMIN only)")
    public ResponseEntity<ModuleResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(moduleService.approve(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'CENTRE_ADMIN')")
    @Operation(summary = "Delete a module (admin only)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        moduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

