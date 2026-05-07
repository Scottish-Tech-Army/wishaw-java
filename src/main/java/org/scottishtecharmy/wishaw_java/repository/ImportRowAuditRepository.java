package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.ImportRowAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImportRowAuditRepository extends JpaRepository<ImportRowAudit, Long> {
    List<ImportRowAudit> findByImportBatchId(Long importBatchId);
}
