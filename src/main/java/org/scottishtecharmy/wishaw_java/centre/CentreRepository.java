package org.scottishtecharmy.wishaw_java.centre;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CentreRepository extends JpaRepository<Centre, Long> {
    Optional<Centre> findByCode(String code);
    boolean existsByCode(String code);
}
