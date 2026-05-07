package org.scottishtecharmy.wishaw_java.module;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByCentreId(Long centreId);
    List<Module> findByApprovedTrue();
}

