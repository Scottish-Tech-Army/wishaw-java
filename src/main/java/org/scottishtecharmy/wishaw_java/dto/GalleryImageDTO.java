package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GalleryImageDTO {
    private Long id;
    private Long tournamentId;
    private String tournamentName;
    private Long matchId;
    private String matchTitle;
    private Long uploadedById;
    private String uploadedByName;
    private String imageUrl;
    private String caption;
    private String overlayTemplate;
    private String createdAt;
}

