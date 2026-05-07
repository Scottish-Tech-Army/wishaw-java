package org.scottishtecharmy.wishaw.repository;

import org.scottishtecharmy.wishaw.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {
}
