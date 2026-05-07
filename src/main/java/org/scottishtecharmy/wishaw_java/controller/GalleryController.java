package org.scottishtecharmy.wishaw_java.controller;

import com.ltc.dto.*;
import org.scottishtecharmy.wishaw_java.dto.ApiResponse;
import org.scottishtecharmy.wishaw_java.dto.GalleryImageDTO;
import org.scottishtecharmy.wishaw_java.service.GalleryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gallery")
@RequiredArgsConstructor
@Tag(name = "Gallery", description = "Image gallery and overlay management APIs")
public class GalleryController {

    private final GalleryService galleryService;

    @GetMapping
    @Operation(summary = "Get all gallery images")
    public ResponseEntity<ApiResponse<List<GalleryImageDTO>>> getAllImages() {
        return ResponseEntity.ok(ApiResponse.success(galleryService.getAllImages()));
    }

    @GetMapping("/tournament/{tournamentId}")
    @Operation(summary = "Get images by tournament")
    public ResponseEntity<ApiResponse<List<GalleryImageDTO>>> getByTournament(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(ApiResponse.success(galleryService.getImagesByTournament(tournamentId)));
    }

    @GetMapping("/match/{matchId}")
    @Operation(summary = "Get images by match")
    public ResponseEntity<ApiResponse<List<GalleryImageDTO>>> getByMatch(@PathVariable Long matchId) {
        return ResponseEntity.ok(ApiResponse.success(galleryService.getImagesByMatch(matchId)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get images uploaded by user")
    public ResponseEntity<ApiResponse<List<GalleryImageDTO>>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(galleryService.getImagesByUser(userId)));
    }

    @PostMapping
    @Operation(summary = "Upload image", description = "Upload a gallery image with optional overlay template")
    public ResponseEntity<ApiResponse<GalleryImageDTO>> uploadImage(
            @RequestBody GalleryImageDTO dto, @RequestParam Long uploaderId) {
        GalleryImageDTO result = galleryService.uploadImage(dto, uploaderId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Image uploaded successfully", result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete image")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long id) {
        galleryService.deleteImage(id);
        return ResponseEntity.ok(ApiResponse.success("Image deleted successfully", null));
    }
}

