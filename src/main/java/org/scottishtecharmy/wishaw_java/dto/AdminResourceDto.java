package org.scottishtecharmy.wishaw_java.dto;

import lombok.*;

/**
 * A file/resource attached to a session.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminResourceDto {
    private Long id;
    /** Original file name, e.g. "Week1-Slides.pptx" */
    private String fileName;
    /** File type: pptx, pdf, video, image, doc, other */
    private String fileType;
    /** Size in bytes */
    private long fileSizeBytes;
    /** Download URL (presigned S3 URL in production) */
    private String url;
    /** ISO-8601 datetime */
    private String uploadedAt;
}
