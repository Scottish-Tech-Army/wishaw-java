package org.scottishtecharmy.wishaw_java.centre;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scottishtecharmy.wishaw_java.centre.dto.CentreRequest;
import org.scottishtecharmy.wishaw_java.centre.dto.CentreResponse;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CentreServiceTest {

    @Mock private CentreRepository centreRepository;

    @InjectMocks private CentreService centreService;

    private Centre testCentre;

    @BeforeEach
    void setUp() {
        testCentre = new Centre();
        testCentre.setId(1L);
        testCentre.setName("Wishaw YMCA");
        testCentre.setCode("WISHAW");
    }

    // ── findAll ─────────────────────────────────────────────────────

    @Test
    void findAll_returnsAllCentres() {
        when(centreRepository.findAll()).thenReturn(List.of(testCentre));

        List<CentreResponse> result = centreService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Wishaw YMCA");
        assertThat(result.get(0).code()).isEqualTo("WISHAW");
    }

    @Test
    void findAll_emptyList() {
        when(centreRepository.findAll()).thenReturn(List.of());

        assertThat(centreService.findAll()).isEmpty();
    }

    // ── findById ────────────────────────────────────────────────────

    @Test
    void findById_success() {
        when(centreRepository.findById(1L)).thenReturn(Optional.of(testCentre));

        CentreResponse response = centreService.findById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Wishaw YMCA");
    }

    @Test
    void findById_notFound_throws() {
        when(centreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> centreService.findById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Centre not found");
    }

    // ── create ──────────────────────────────────────────────────────

    @Test
    void create_success() {
        CentreRequest request = new CentreRequest("Glasgow YMCA", "glasgow");
        when(centreRepository.existsByCode("glasgow")).thenReturn(false);
        when(centreRepository.save(any(Centre.class))).thenAnswer(inv -> {
            Centre c = inv.getArgument(0);
            c.setId(2L);
            return c;
        });

        CentreResponse response = centreService.create(request);

        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.name()).isEqualTo("Glasgow YMCA");
        assertThat(response.code()).isEqualTo("GLASGOW");
    }

    @Test
    void create_convertsCodeToUpperCase() {
        CentreRequest request = new CentreRequest("Test", "lower");
        when(centreRepository.existsByCode("lower")).thenReturn(false);
        when(centreRepository.save(any(Centre.class))).thenAnswer(inv -> inv.getArgument(0));

        CentreResponse response = centreService.create(request);

        assertThat(response.code()).isEqualTo("LOWER");
    }

    @Test
    void create_duplicateCode_throws() {
        CentreRequest request = new CentreRequest("Wishaw 2", "WISHAW");
        when(centreRepository.existsByCode("WISHAW")).thenReturn(true);

        assertThatThrownBy(() -> centreService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Centre code already exists");

        verify(centreRepository, never()).save(any());
    }

    // ── update ──────────────────────────────────────────────────────

    @Test
    void update_success() {
        CentreRequest request = new CentreRequest("Updated Name", "updated");
        when(centreRepository.findById(1L)).thenReturn(Optional.of(testCentre));
        when(centreRepository.save(any(Centre.class))).thenAnswer(inv -> inv.getArgument(0));

        CentreResponse response = centreService.update(1L, request);

        assertThat(response.name()).isEqualTo("Updated Name");
        assertThat(response.code()).isEqualTo("UPDATED");
    }

    @Test
    void update_notFound_throws() {
        CentreRequest request = new CentreRequest("X", "X");
        when(centreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> centreService.update(99L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Centre not found");
    }

    // ── delete ──────────────────────────────────────────────────────

    @Test
    void delete_success() {
        when(centreRepository.existsById(1L)).thenReturn(true);

        centreService.delete(1L);

        verify(centreRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throws() {
        when(centreRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> centreService.delete(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Centre not found");
    }
}

