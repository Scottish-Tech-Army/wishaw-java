package org.scottishtecharmy.wishaw_java.centre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scottishtecharmy.wishaw_java.centre.dto.CentreRequest;
import org.scottishtecharmy.wishaw_java.centre.dto.CentreResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CentreService {

    private final CentreRepository centreRepository;

    public List<CentreResponse> findAll() {
        log.debug("Fetching all centres");
        return centreRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public CentreResponse findById(Long id) {
        log.debug("Fetching centre by id={}", id);
        Centre centre = centreRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Centre not found: id={}", id);
                    return new IllegalArgumentException("Centre not found: " + id);
                });
        return toResponse(centre);
    }

    public CentreResponse create(CentreRequest request) {
        log.info("Creating centre: name='{}', code='{}'", request.name(), request.code());
        if (centreRepository.existsByCode(request.code())) {
            log.warn("Centre creation failed — code '{}' already exists", request.code());
            throw new IllegalArgumentException("Centre code already exists: " + request.code());
        }
        Centre centre = new Centre();
        centre.setName(request.name());
        centre.setCode(request.code().toUpperCase());
        CentreResponse response = toResponse(centreRepository.save(centre));
        log.info("Centre created: id={}, code='{}'", response.id(), response.code());
        return response;
    }

    public CentreResponse update(Long id, CentreRequest request) {
        log.info("Updating centre: id={}", id);
        Centre centre = centreRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Centre update failed — not found: id={}", id);
                    return new IllegalArgumentException("Centre not found: " + id);
                });
        centre.setName(request.name());
        centre.setCode(request.code().toUpperCase());
        return toResponse(centreRepository.save(centre));
    }

    public void delete(Long id) {
        log.info("Deleting centre: id={}", id);
        if (!centreRepository.existsById(id)) {
            log.warn("Centre deletion failed — not found: id={}", id);
            throw new IllegalArgumentException("Centre not found: " + id);
        }
        centreRepository.deleteById(id);
        log.info("Centre deleted: id={}", id);
    }

    private CentreResponse toResponse(Centre centre) {
        return new CentreResponse(centre.getId(), centre.getName(), centre.getCode(), centre.getCreatedAt());
    }
}
