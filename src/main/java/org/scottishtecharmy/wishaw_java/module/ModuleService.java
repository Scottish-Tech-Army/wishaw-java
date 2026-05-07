package org.scottishtecharmy.wishaw_java.module;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scottishtecharmy.wishaw_java.centre.Centre;
import org.scottishtecharmy.wishaw_java.centre.CentreRepository;
import org.scottishtecharmy.wishaw_java.module.dto.ModuleRequest;
import org.scottishtecharmy.wishaw_java.module.dto.ModuleResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CentreRepository centreRepository;

    public List<ModuleResponse> findAll() {
        log.debug("Fetching all modules");
        return moduleRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ModuleResponse findById(Long id) {
        log.debug("Fetching module by id={}", id);
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Module not found: id={}", id);
                    return new IllegalArgumentException("Module not found: " + id);
                });
        return toResponse(module);
    }

    public List<ModuleResponse> findByCentre(Long centreId) {
        log.debug("Fetching modules for centreId={}", centreId);
        return moduleRepository.findByCentreId(centreId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ModuleResponse> findApproved() {
        log.debug("Fetching approved modules");
        return moduleRepository.findByApprovedTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public ModuleResponse create(ModuleRequest request) {
        log.info("Creating module: name='{}', centreId={}", request.name(), request.centreId());
        Centre centre = centreRepository.findById(request.centreId())
                .orElseThrow(() -> new IllegalArgumentException("Centre not found: " + request.centreId()));

        Module module = new Module();
        module.setName(request.name());
        module.setDescription(request.description());
        module.setApproved(false);
        module.setCentre(centre);
        ModuleResponse response = toResponse(moduleRepository.save(module));
        log.info("Module created: id={}, name='{}', approved=false", response.id(), response.name());
        return response;
    }

    public ModuleResponse update(Long id, ModuleRequest request) {
        log.info("Updating module: id={}", id);
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Module update failed — not found: id={}", id);
                    return new IllegalArgumentException("Module not found: " + id);
                });

        Centre centre = centreRepository.findById(request.centreId())
                .orElseThrow(() -> new IllegalArgumentException("Centre not found: " + request.centreId()));

        module.setName(request.name());
        module.setDescription(request.description());
        module.setCentre(centre);
        return toResponse(moduleRepository.save(module));
    }

    public ModuleResponse approve(Long id) {
        log.info("Approving module: id={}", id);
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Module approval failed — not found: id={}", id);
                    return new IllegalArgumentException("Module not found: " + id);
                });
        module.setApproved(true);
        ModuleResponse response = toResponse(moduleRepository.save(module));
        log.info("Module approved: id={}, name='{}'", id, response.name());
        return response;
    }

    public void delete(Long id) {
        log.info("Deleting module: id={}", id);
        if (!moduleRepository.existsById(id)) {
            log.warn("Module deletion failed — not found: id={}", id);
            throw new IllegalArgumentException("Module not found: " + id);
        }
        moduleRepository.deleteById(id);
        log.info("Module deleted: id={}", id);
    }

    private ModuleResponse toResponse(Module module) {
        return new ModuleResponse(
                module.getId(),
                module.getName(),
                module.getDescription(),
                module.isApproved(),
                module.getCentre() != null ? module.getCentre().getId() : null,
                module.getCentre() != null ? module.getCentre().getName() : null,
                module.getCreatedAt()
        );
    }
}
