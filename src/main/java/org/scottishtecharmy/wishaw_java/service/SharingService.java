package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.ShareableCardDTO;
import org.scottishtecharmy.wishaw_java.entity.User;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class SharingService {

    private final UserRepository userRepository;

    public ShareableCardDTO generateShareableCard(Long playerId, String achievement,
                                                   String description, String tournamentName,
                                                   String sportType) {
        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player", playerId));

        String shareText = String.format("%s achieved '%s' in %s (%s)! \uD83C\uDFC6",
                player.getFullName(), achievement, tournamentName, sportType);

        String encodedText = URLEncoder.encode(shareText, StandardCharsets.UTF_8);
        String shareUrl = String.format("/share/player/%d/achievement?text=%s", playerId, encodedText);
        String whatsappUrl = "https://wa.me/?text=" + encodedText;

        return ShareableCardDTO.builder()
                .playerId(player.getId())
                .playerName(player.getFullName())
                .profilePhotoUrl(player.getProfilePhotoUrl())
                .achievement(achievement)
                .description(description)
                .tournamentName(tournamentName)
                .sportType(sportType)
                .shareUrl(shareUrl)
                .whatsappShareUrl(whatsappUrl)
                .build();
    }
}

