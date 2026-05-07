package org.scottishtecharmy.wishaw_java.centre;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw_java.centre.dto.CentreRequest;
import org.scottishtecharmy.wishaw_java.centre.dto.CentreResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/centres")
@RequiredArgsConstructor
@Tag(name = "Centres", description = "YMCA centre management")
public class CentreController {

    private final CentreService centreService;

    @GetMapping
    @Operation(summary = "List all centres")
    public ResponseEntity<List<CentreResponse>> getAll() {
        return ResponseEntity.ok(centreService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get centre by ID")
    public ResponseEntity<CentreResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(centreService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @Operation(summary = "Create a new centre (MAIN_ADMIN only)")
    public ResponseEntity<CentreResponse> create(@Valid @RequestBody CentreRequest request) {
        return ResponseEntity.ok(centreService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @Operation(summary = "Update a centre (MAIN_ADMIN only)")
    public ResponseEntity<CentreResponse> update(@PathVariable Long id, @Valid @RequestBody CentreRequest request) {
        return ResponseEntity.ok(centreService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @Operation(summary = "Delete a centre (MAIN_ADMIN only)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        centreService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

