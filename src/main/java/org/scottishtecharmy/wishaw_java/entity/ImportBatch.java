package org.scottishtecharmy.wishaw_java.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.scottishtecharmy.wishaw_java.enums.ImportStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "import_batches")
@Getter
@Setter
@NoArgsConstructor
public class ImportBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String importType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImportStatus status = ImportStatus.UPLOADED;

    private String checksum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private UserAccount uploadedBy;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    @Column(columnDefinition = "CLOB")
    private String summaryJson;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
