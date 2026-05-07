package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.GalleryImageDTO;
import com.ltc.entity.*;
import com.ltc.exception.*;
import com.ltc.repository.*;
import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw_java.entity.GalleryImage;
import org.scottishtecharmy.wishaw_java.entity.Match;
import org.scottishtecharmy.wishaw_java.entity.Tournament;
import org.scottishtecharmy.wishaw_java.entity.User;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.repository.GalleryImageRepository;
import org.scottishtecharmy.wishaw_java.repository.MatchRepository;
import org.scottishtecharmy.wishaw_java.repository.TournamentRepository;
import org.scottishtecharmy.wishaw_java.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GalleryService {

    private final GalleryImageRepository galleryImageRepository;
    private final TournamentRepository tournamentRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    public List<GalleryImageDTO> getAllImages() {
        return galleryImageRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<GalleryImageDTO> getImagesByTournament(Long tournamentId) {
        return galleryImageRepository.findByTournamentId(tournamentId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<GalleryImageDTO> getImagesByMatch(Long matchId) {
        return galleryImageRepository.findByMatchId(matchId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<GalleryImageDTO> getImagesByUser(Long userId) {
        return galleryImageRepository.findByUploadedById(userId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public GalleryImageDTO uploadImage(GalleryImageDTO dto, Long uploaderId) {
        User uploader = userRepository.findById(uploaderId)
                .orElseThrow(() -> new ResourceNotFoundException("User", uploaderId));

        GalleryImage image = GalleryImage.builder()
                .imageUrl(dto.getImageUrl()).caption(dto.getCaption())
                .overlayTemplate(dto.getOverlayTemplate()).uploadedBy(uploader).build();

        if (dto.getTournamentId() != null) {
            Tournament tournament = tournamentRepository.findById(dto.getTournamentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tournament", dto.getTournamentId()));
            image.setTournament(tournament);
        }
        if (dto.getMatchId() != null) {
            Match match = matchRepository.findById(dto.getMatchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Match", dto.getMatchId()));
            image.setMatch(match);
        }

        GalleryImage saved = galleryImageRepository.save(image);
        return mapToDTO(saved);
    }

    @Transactional
    public void deleteImage(Long id) {
        if (!galleryImageRepository.existsById(id)) throw new ResourceNotFoundException("GalleryImage", id);
        galleryImageRepository.deleteById(id);
    }

    private GalleryImageDTO mapToDTO(GalleryImage g) {
        return GalleryImageDTO.builder()
                .id(g.getId())
                .tournamentId(g.getTournament() != null ? g.getTournament().getId() : null)
                .tournamentName(g.getTournament() != null ? g.getTournament().getName() : null)
                .matchId(g.getMatch() != null ? g.getMatch().getId() : null)
                .matchTitle(g.getMatch() != null ? g.getMatch().getMatchTitle() : null)
                .uploadedById(g.getUploadedBy() != null ? g.getUploadedBy().getId() : null)
                .uploadedByName(g.getUploadedBy() != null ? g.getUploadedBy().getFullName() : null)
                .imageUrl(g.getImageUrl()).caption(g.getCaption())
                .overlayTemplate(g.getOverlayTemplate())
                .createdAt(g.getCreatedAt() != null ? g.getCreatedAt().toString() : null)
                .build();
    }
}

