package org.scottishtecharmy.wishaw.repository;

import org.scottishtecharmy.wishaw.entity.Module;
import org.scottishtecharmy.wishaw.entity.Centre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByCentre(Centre centre);
}
