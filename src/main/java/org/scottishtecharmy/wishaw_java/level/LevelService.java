package org.scottishtecharmy.wishaw_java.level;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scottishtecharmy.wishaw_java.level.dto.LevelRequest;
import org.scottishtecharmy.wishaw_java.level.dto.LevelResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LevelService {

    private final LevelRepository levelRepository;

    /**
     * Data-driven level calculation: looks up level thresholds from the database.
     * Falls back to "UNRANKED" if no levels are configured.
     */
    public String calculateLevel(int points) {
        List<Level> levels = levelRepository.findAllByOrderByDisplayOrderAsc();
        for (Level level : levels) {
            if (points >= level.getMinPoints()
                    && (level.getMaxPoints() == -1 || points <= level.getMaxPoints())) {
                log.debug("Calculated level='{}' for points={}", level.getName(), points);
                return level.getName();
            }
        }
        log.debug("No matching level for points={}, returning UNRANKED", points);
        return "UNRANKED";
    }

    // --- CRUD ---

    public List<LevelResponse> findAll() {
        log.debug("Fetching all levels");
        return levelRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    public LevelResponse findById(Long id) {
        log.debug("Fetching level by id={}", id);
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Level not found: id={}", id);
                    return new IllegalArgumentException("Level not found: " + id);
                });
        return toResponse(level);
    }

    public LevelResponse create(LevelRequest request) {
        log.info("Creating level: name='{}', range=[{}-{}]", request.name(), request.minPoints(), request.maxPoints());
        Level level = new Level();
        level.setName(request.name());
        level.setMinPoints(request.minPoints());
        level.setMaxPoints(request.maxPoints());
        level.setDisplayOrder(request.displayOrder() != null ? request.displayOrder() : 0);
        LevelResponse response = toResponse(levelRepository.save(level));
        log.info("Level created: id={}, name='{}'", response.id(), response.name());
        return response;
    }

    public LevelResponse update(Long id, LevelRequest request) {
        log.info("Updating level: id={}", id);
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Level update failed — not found: id={}", id);
                    return new IllegalArgumentException("Level not found: " + id);
                });
        level.setName(request.name());
        level.setMinPoints(request.minPoints());
        level.setMaxPoints(request.maxPoints());
        level.setDisplayOrder(request.displayOrder() != null ? request.displayOrder() : level.getDisplayOrder());
        return toResponse(levelRepository.save(level));
    }

    public void delete(Long id) {
        log.info("Deleting level: id={}", id);
        if (!levelRepository.existsById(id)) {
            log.warn("Level deletion failed — not found: id={}", id);
            throw new IllegalArgumentException("Level not found: " + id);
        }
        levelRepository.deleteById(id);
        log.info("Level deleted: id={}", id);
    }

    private LevelResponse toResponse(Level level) {
        return new LevelResponse(
                level.getId(),
                level.getName(),
                level.getMinPoints(),
                level.getMaxPoints(),
                level.getDisplayOrder(),
                level.getCreatedAt()
        );
    }
}
