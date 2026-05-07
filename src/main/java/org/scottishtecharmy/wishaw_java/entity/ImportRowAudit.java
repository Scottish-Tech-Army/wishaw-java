package org.scottishtecharmy.wishaw_java.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.scottishtecharmy.wishaw_java.enums.ImportRowStatus;

@Entity
@Table(name = "import_row_audits")
@Getter
@Setter
@NoArgsConstructor
public class ImportRowAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_batch_id", nullable = false)
    private ImportBatch importBatch;

    private String sourceSection;
    private int sourceRowNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImportRowStatus status;

    private String message;

    @Column(columnDefinition = "CLOB")
    private String rawDataJson;
}
