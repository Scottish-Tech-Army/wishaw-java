package org.scottishtecharmy.wishaw_java.service.admin;

import org.scottishtecharmy.wishaw_java.dto.request.CreateCentreRequest;
import org.scottishtecharmy.wishaw_java.dto.response.CentreResponse;
import org.scottishtecharmy.wishaw_java.entity.Centre;
import org.scottishtecharmy.wishaw_java.exception.BadRequestException;
import org.scottishtecharmy.wishaw_java.exception.DuplicateResourceException;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.mapper.DtoMapper;
import org.scottishtecharmy.wishaw_java.repository.CentreRepository;
import org.scottishtecharmy.wishaw_java.repository.GroupRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CentreAdminService {

    private final CentreRepository centreRepository;
    private final GroupRepository groupRepository;
    private final UserAccountRepository userAccountRepository;

    public CentreResponse createCentre(CreateCentreRequest request) {
        if (centreRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Centre code already exists: " + request.getCode());
        }

        Centre centre = new Centre();
        centre.setName(request.getName());
        centre.setCode(request.getCode());
        return DtoMapper.toCentreResponse(centreRepository.save(centre));
    }

    @Transactional(readOnly = true)
    public List<CentreResponse> listCentres() {
        return centreRepository.findAll().stream()
                .map(DtoMapper::toCentreResponse)
                .toList();
    }

    public CentreResponse updateCentre(Long centreId, CreateCentreRequest request) {
        Centre centre = centreRepository.findById(centreId)
                .orElseThrow(() -> new ResourceNotFoundException("Centre not found: " + centreId));

        if (!centre.getCode().equals(request.getCode()) && centreRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Centre code already exists: " + request.getCode());
        }

        centre.setName(request.getName());
        centre.setCode(request.getCode());
        return DtoMapper.toCentreResponse(centreRepository.save(centre));
    }

    public void deleteCentre(Long centreId) {
        centreRepository.findById(centreId)
                .orElseThrow(() -> new ResourceNotFoundException("Centre not found: " + centreId));

        if (!userAccountRepository.findByCentreId(centreId).isEmpty()) {
            throw new BadRequestException("Cannot delete centre: users are still assigned to it");
        }
        if (!groupRepository.findByCentreId(centreId).isEmpty()) {
            throw new BadRequestException("Cannot delete centre: groups are still assigned to it");
        }

        centreRepository.deleteById(centreId);
    }
}
