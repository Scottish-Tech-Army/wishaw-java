package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ModuleRepository extends JpaRepository<Module, Long> {

    @Modifying
    @Query("UPDATE Module m SET m.createdBy = null WHERE m.createdBy.id = :userId")
    void nullifyCreatedBy(@Param("userId") Long userId);
}
