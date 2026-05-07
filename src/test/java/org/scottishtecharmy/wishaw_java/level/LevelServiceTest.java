package org.scottishtecharmy.wishaw_java.level;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scottishtecharmy.wishaw_java.level.dto.LevelRequest;
import org.scottishtecharmy.wishaw_java.level.dto.LevelResponse;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LevelServiceTest {

    @Mock private LevelRepository levelRepository;

    @InjectMocks private LevelService levelService;

    private Level bronze;
    private Level silver;
    private Level gold;
    private Level platinum;

    @BeforeEach
    void setUp() {
        bronze = new Level();
        bronze.setId(1L);
        bronze.setName("BRONZE");
        bronze.setMinPoints(0);
        bronze.setMaxPoints(99);
        bronze.setDisplayOrder(1);

        silver = new Level();
        silver.setId(2L);
        silver.setName("SILVER");
        silver.setMinPoints(100);
        silver.setMaxPoints(249);
        silver.setDisplayOrder(2);

        gold = new Level();
        gold.setId(3L);
        gold.setName("GOLD");
        gold.setMinPoints(250);
        gold.setMaxPoints(499);
        gold.setDisplayOrder(3);

        platinum = new Level();
        platinum.setId(4L);
        platinum.setName("PLATINUM");
        platinum.setMinPoints(500);
        platinum.setMaxPoints(-1); // unlimited
        platinum.setDisplayOrder(4);
    }

    // ── calculateLevel ──────────────────────────────────��───────────

    @Test
    void calculateLevel_returnsBronze_forLowPoints() {
        when(levelRepository.findAllByOrderByDisplayOrderAsc())
                .thenReturn(List.of(bronze, silver, gold, platinum));

        assertThat(levelService.calculateLevel(0)).isEqualTo("BRONZE");
        assertThat(levelService.calculateLevel(50)).isEqualTo("BRONZE");
        assertThat(levelService.calculateLevel(99)).isEqualTo("BRONZE");
    }

    @Test
    void calculateLevel_returnsSilver() {
        when(levelRepository.findAllByOrderByDisplayOrderAsc())
                .thenReturn(List.of(bronze, silver, gold, platinum));

        assertThat(levelService.calculateLevel(100)).isEqualTo("SILVER");
        assertThat(levelService.calculateLevel(249)).isEqualTo("SILVER");
    }

    @Test
    void calculateLevel_returnsGold() {
        when(levelRepository.findAllByOrderByDisplayOrderAsc())
                .thenReturn(List.of(bronze, silver, gold, platinum));

        assertThat(levelService.calculateLevel(250)).isEqualTo("GOLD");
        assertThat(levelService.calculateLevel(499)).isEqualTo("GOLD");
    }

    @Test
    void calculateLevel_returnsPlatinum_forHighPoints() {
        when(levelRepository.findAllByOrderByDisplayOrderAsc())
                .thenReturn(List.of(bronze, silver, gold, platinum));

        assertThat(levelService.calculateLevel(500)).isEqualTo("PLATINUM");
        assertThat(levelService.calculateLevel(10000)).isEqualTo("PLATINUM");
    }

    @Test
    void calculateLevel_returnsUnranked_whenNoLevelsConfigured() {
        when(levelRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of());

        assertThat(levelService.calculateLevel(50)).isEqualTo("UNRANKED");
    }

    @Test
    void calculateLevel_returnsUnranked_whenNoLevelMatches() {
        // Only bronze defined with range 0-99; points 200 doesn't match
        when(levelRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of(bronze));

        assertThat(levelService.calculateLevel(200)).isEqualTo("UNRANKED");
    }

    // ── findAll ─────────────────────────────────────────────────────

    @Test
    void findAll_returnsOrderedLevels() {
        when(levelRepository.findAllByOrderByDisplayOrderAsc())
                .thenReturn(List.of(bronze, silver, gold, platinum));

        List<LevelResponse> result = levelService.findAll();

        assertThat(result).hasSize(4);
        assertThat(result.get(0).name()).isEqualTo("BRONZE");
        assertThat(result.get(3).name()).isEqualTo("PLATINUM");
    }

    @Test
    void findAll_emptyList() {
        when(levelRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of());
        assertThat(levelService.findAll()).isEmpty();
    }

    // ── findById ────────────────────────────────────��───────────────

    @Test
    void findById_success() {
        when(levelRepository.findById(1L)).thenReturn(Optional.of(bronze));

        LevelResponse response = levelService.findById(1L);

        assertThat(response.name()).isEqualTo("BRONZE");
        assertThat(response.minPoints()).isEqualTo(0);
        assertThat(response.maxPoints()).isEqualTo(99);
    }

    @Test
    void findById_notFound_throws() {
        when(levelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> levelService.findById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Level not found");
    }

    // ── create ──────────────────────────────────────────────────────

    @Test
    void create_success() {
        LevelRequest request = new LevelRequest("DIAMOND", 1000, -1, 5);
        when(levelRepository.save(any(Level.class))).thenAnswer(inv -> {
            Level l = inv.getArgument(0);
            l.setId(5L);
            return l;
        });

        LevelResponse response = levelService.create(request);

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.name()).isEqualTo("DIAMOND");
        assertThat(response.minPoints()).isEqualTo(1000);
        assertThat(response.maxPoints()).isEqualTo(-1);
        assertThat(response.displayOrder()).isEqualTo(5);
    }

    @Test
    void create_withNullDisplayOrder_defaultsToZero() {
        LevelRequest request = new LevelRequest("TEST", 0, 10, null);
        when(levelRepository.save(any(Level.class))).thenAnswer(inv -> {
            Level l = inv.getArgument(0);
            l.setId(6L);
            return l;
        });

        LevelResponse response = levelService.create(request);

        assertThat(response.displayOrder()).isEqualTo(0);
    }

    // ── update ──────────────────────────────────────────────────────

    @Test
    void update_success() {
        LevelRequest request = new LevelRequest("UPDATED", 10, 200, 99);
        when(levelRepository.findById(1L)).thenReturn(Optional.of(bronze));
        when(levelRepository.save(any(Level.class))).thenAnswer(inv -> inv.getArgument(0));

        LevelResponse response = levelService.update(1L, request);

        assertThat(response.name()).isEqualTo("UPDATED");
        assertThat(response.minPoints()).isEqualTo(10);
        assertThat(response.maxPoints()).isEqualTo(200);
        assertThat(response.displayOrder()).isEqualTo(99);
    }

    @Test
    void update_withNullDisplayOrder_keepsExisting() {
        LevelRequest request = new LevelRequest("BRONZE", 0, 99, null);
        when(levelRepository.findById(1L)).thenReturn(Optional.of(bronze));
        when(levelRepository.save(any(Level.class))).thenAnswer(inv -> inv.getArgument(0));

        LevelResponse response = levelService.update(1L, request);

        assertThat(response.displayOrder()).isEqualTo(1); // keeps original
    }

    @Test
    void update_notFound_throws() {
        LevelRequest request = new LevelRequest("X", 0, 10, 0);
        when(levelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> levelService.update(99L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Level not found");
    }

    // ── delete ──────────────────────────────────────────────────────

    @Test
    void delete_success() {
        when(levelRepository.existsById(1L)).thenReturn(true);

        levelService.delete(1L);

        verify(levelRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throws() {
        when(levelRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> levelService.delete(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Level not found");
    }
}

