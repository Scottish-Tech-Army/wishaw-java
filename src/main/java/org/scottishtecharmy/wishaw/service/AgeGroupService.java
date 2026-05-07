package org.scottishtecharmy.wishaw.service;

import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw.entity.*;
import org.scottishtecharmy.wishaw.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgeGroupService {

    private final AgeGroupRepository ageGroupRepository;

    @Transactional(readOnly = true)
    public List<AgeGroup> findAll() {
        return ageGroupRepository.findAll();
    }

    @Transactional(readOnly = true)
    public AgeGroup findById(Long id) {
        return ageGroupRepository.findById(id).orElse(null);
    }

    @Transactional
    public AgeGroup save(AgeGroup ageGroup) {
        return ageGroupRepository.save(ageGroup);
    }

    @Transactional
    public void delete(Long id) {
        ageGroupRepository.deleteById(id);
    }
}
