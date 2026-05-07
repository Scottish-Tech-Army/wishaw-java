package org.scottishtecharmy.wishaw_java.controller;

import com.ltc.dto.*;
import org.scottishtecharmy.wishaw_java.dto.ApiResponse;
import org.scottishtecharmy.wishaw_java.dto.ShareableCardDTO;
import org.scottishtecharmy.wishaw_java.service.SharingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sharing")
@RequiredArgsConstructor
@Tag(name = "Social Sharing", description = "Achievement sharing via WhatsApp, links, and downloadable cards")
public class SharingController {

    private final SharingService sharingService;

    @GetMapping("/card")
    @Operation(summary = "Generate shareable achievement card",
               description = "Generate a shareable card with WhatsApp and link sharing URLs for a player achievement")
    public ResponseEntity<ApiResponse<ShareableCardDTO>> generateShareableCard(
            @RequestParam Long playerId,
            @RequestParam String achievement,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String tournamentName,
            @RequestParam(required = false) String sportType) {
        ShareableCardDTO card = sharingService.generateShareableCard(
                playerId, achievement, description, tournamentName, sportType);
        return ResponseEntity.ok(ApiResponse.success(card));
    }
}

