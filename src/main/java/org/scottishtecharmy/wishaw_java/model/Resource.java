package org.scottishtecharmy.wishaw_java.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * A file/resource attached to a session.
 */
@Entity
@Table(name = "resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    /** Original file name, e.g. "Week1-Slides.pptx" */
    private String fileName;

    /** File type: pptx, pdf, video, image, doc, other */
    private String fileType;

    /** Size in bytes */
    private long fileSizeBytes;

    /** Download URL (presigned S3 URL in production) */
    @Column(length = 1000)
    private String url;

    /** ISO-8601 datetime */
    private String uploadedAt;
}
