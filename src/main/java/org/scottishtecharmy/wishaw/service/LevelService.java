package org.scottishtecharmy.wishaw.service;

import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw.entity.*;
import org.scottishtecharmy.wishaw.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LevelService {

    private final LevelRepository levelRepository;

    @Transactional(readOnly = true)
    public List<Level> findAll() {
        return levelRepository.findAllByOrderByDisplayOrderAsc();
    }

    @Transactional(readOnly = true)
    public Level findById(Long id) {
        return levelRepository.findById(id).orElse(null);
    }

    @Transactional
    public Level save(Level level) {
        return levelRepository.save(level);
    }

    @Transactional
    public void delete(Long id) {
        levelRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Level getLevelForPoints(int points) {
        return levelRepository.findTopByMinPointsLessThanEqualOrderByMinPointsDesc(points);
    }
}
