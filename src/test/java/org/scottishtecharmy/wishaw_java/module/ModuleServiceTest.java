package org.scottishtecharmy.wishaw_java.module;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scottishtecharmy.wishaw_java.centre.Centre;
import org.scottishtecharmy.wishaw_java.centre.CentreRepository;
import org.scottishtecharmy.wishaw_java.module.dto.ModuleRequest;
import org.scottishtecharmy.wishaw_java.module.dto.ModuleResponse;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModuleServiceTest {

    @Mock private ModuleRepository moduleRepository;
    @Mock private CentreRepository centreRepository;

    @InjectMocks private ModuleService moduleService;

    private Module testModule;
    private Centre testCentre;

    @BeforeEach
    void setUp() {
        testCentre = new Centre();
        testCentre.setId(1L);
        testCentre.setName("Wishaw YMCA");
        testCentre.setCode("WISHAW");

        testModule = new Module();
        testModule.setId(1L);
        testModule.setName("Minecraft");
        testModule.setDescription("Block building game");
        testModule.setApproved(false);
        testModule.setCentre(testCentre);
    }

    // ── findAll ─────────────────────────────────────────────────────

    @Test
    void findAll_returnsMappedResponses() {
        when(moduleRepository.findAll()).thenReturn(List.of(testModule));

        List<ModuleResponse> result = moduleService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Minecraft");
        assertThat(result.get(0).centreName()).isEqualTo("Wishaw YMCA");
        assertThat(result.get(0).approved()).isFalse();
    }

    @Test
    void findAll_emptyList() {
        when(moduleRepository.findAll()).thenReturn(List.of());
        assertThat(moduleService.findAll()).isEmpty();
    }

    // ── findById ────────────────────────────────────────────────────

    @Test
    void findById_success() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(testModule));

        ModuleResponse response = moduleService.findById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.description()).isEqualTo("Block building game");
    }

    @Test
    void findById_notFound_throws() {
        when(moduleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> moduleService.findById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Module not found");
    }

    // ── findByCentre ────────────────────────────────────────────────

    @Test
    void findByCentre_returnsFilteredModules() {
        when(moduleRepository.findByCentreId(1L)).thenReturn(List.of(testModule));

        List<ModuleResponse> result = moduleService.findByCentre(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).centreId()).isEqualTo(1L);
    }

    // ── findApproved ────────────────────────────────────────────────

    @Test
    void findApproved_returnsOnlyApproved() {
        testModule.setApproved(true);
        when(moduleRepository.findByApprovedTrue()).thenReturn(List.of(testModule));

        List<ModuleResponse> result = moduleService.findApproved();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).approved()).isTrue();
    }

    // ── create ──────────────────────────────────────────────────────

    @Test
    void create_success_defaultsToNotApproved() {
        ModuleRequest request = new ModuleRequest("Fortnite", "Battle royale", 1L);
        when(centreRepository.findById(1L)).thenReturn(Optional.of(testCentre));
        when(moduleRepository.save(any(Module.class))).thenAnswer(inv -> {
            Module m = inv.getArgument(0);
            m.setId(2L);
            return m;
        });

        ModuleResponse response = moduleService.create(request);

        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.name()).isEqualTo("Fortnite");
        assertThat(response.approved()).isFalse();
    }

    @Test
    void create_centreNotFound_throws() {
        ModuleRequest request = new ModuleRequest("Fortnite", "desc", 999L);
        when(centreRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> moduleService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Centre not found");
    }

    // ── update ──────────────────────────────────────────────────────

    @Test
    void update_success() {
        ModuleRequest request = new ModuleRequest("Updated Module", "Updated desc", 1L);
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(testModule));
        when(centreRepository.findById(1L)).thenReturn(Optional.of(testCentre));
        when(moduleRepository.save(any(Module.class))).thenAnswer(inv -> inv.getArgument(0));

        ModuleResponse response = moduleService.update(1L, request);

        assertThat(response.name()).isEqualTo("Updated Module");
        assertThat(response.description()).isEqualTo("Updated desc");
    }

    @Test
    void update_notFound_throws() {
        ModuleRequest request = new ModuleRequest("X", "X", 1L);
        when(moduleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> moduleService.update(99L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Module not found");
    }

    @Test
    void update_centreNotFound_throws() {
        ModuleRequest request = new ModuleRequest("X", "X", 999L);
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(testModule));
        when(centreRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> moduleService.update(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Centre not found");
    }

    // ── approve ─────────────────────────────────────────────────────

    @Test
    void approve_success() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(testModule));
        when(moduleRepository.save(any(Module.class))).thenAnswer(inv -> inv.getArgument(0));

        ModuleResponse response = moduleService.approve(1L);

        assertThat(response.approved()).isTrue();
    }

    @Test
    void approve_notFound_throws() {
        when(moduleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> moduleService.approve(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Module not found");
    }

    // ── delete ──────────────────────────────────────────────────────

    @Test
    void delete_success() {
        when(moduleRepository.existsById(1L)).thenReturn(true);

        moduleService.delete(1L);

        verify(moduleRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throws() {
        when(moduleRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> moduleService.delete(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Module not found");
    }

    // ── toResponse with null centre ─────────────────────────────────

    @Test
    void toResponse_withNullCentre_returnsNulls() {
        testModule.setCentre(null);
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(testModule));

        ModuleResponse response = moduleService.findById(1L);

        assertThat(response.centreId()).isNull();
        assertThat(response.centreName()).isNull();
    }
}

