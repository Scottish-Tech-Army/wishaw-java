package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.ImportBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImportBatchRepository extends JpaRepository<ImportBatch, Long> {
    Optional<ImportBatch> findByChecksum(String checksum);
}
