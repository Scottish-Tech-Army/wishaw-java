package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.model.Centre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CentreRepository extends JpaRepository<Centre, Long> {
}
