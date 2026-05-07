package org.scottishtecharmy.wishaw.service;

import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw.entity.*;
import org.scottishtecharmy.wishaw.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final SubBadgeRepository subBadgeRepository;
    private final SkillRepository skillRepository;

    @Transactional(readOnly = true)
    public List<Badge> findAll() {
        return badgeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Badge> findByCentre(Centre centre) {
        return badgeRepository.findByCentre(centre);
    }

    @Transactional(readOnly = true)
    public List<Badge> findGlobal() {
        return badgeRepository.findByCentreIsNull();
    }

    @Transactional(readOnly = true)
    public Badge findById(Long id) {
        return badgeRepository.findById(id).orElse(null);
    }

    @Transactional
    public Badge save(Badge badge) {
        return badgeRepository.save(badge);
    }

    @Transactional
    public void delete(Long id) {
        badgeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<SubBadge> findAllSubBadges() {
        return subBadgeRepository.findAll();
    }

    @Transactional
    public SubBadge saveSubBadge(SubBadge subBadge) {
        return subBadgeRepository.save(subBadge);
    }

    @Transactional
    public SubBadge updateSubBadge(SubBadge subBadge) {
        return subBadgeRepository.save(subBadge);
    }

    @Transactional
    public void deleteSubBadge(Long id) {
        subBadgeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Skill> findAllSkills() {
        return skillRepository.findAll();
    }

    @Transactional
    public Skill saveSkill(Skill skill) {
        return skillRepository.save(skill);
    }
}
