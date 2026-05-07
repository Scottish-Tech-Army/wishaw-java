package org.scottishtecharmy.wishaw_java.service.importer;

import org.scottishtecharmy.wishaw_java.dto.request.ImportPlayerMappingRequest;
import org.scottishtecharmy.wishaw_java.dto.response.ImportPreviewResponse;
import org.scottishtecharmy.wishaw_java.entity.ImportBatch;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.enums.Role;
import org.scottishtecharmy.wishaw_java.exception.BadRequestException;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.repository.ImportBatchRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerMappingService {

    private final ImportBatchRepository importBatchRepository;
    private final ImportPreviewService importPreviewService;
    private final UserAccountRepository userAccountRepository;

    public ImportPreviewResponse mapPlayers(Long batchId, ImportPlayerMappingRequest request) {
        ImportBatch batch = importBatchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Import batch not found: " + batchId));
        ImportBatchState state = importPreviewService.loadState(batch);

        if (request.getPlayerMappings() != null) {
            for (Map.Entry<String, Long> entry : request.getPlayerMappings().entrySet()) {
                if (entry.getValue() != null) {
                    UserAccount mappedUser = userAccountRepository.findById(entry.getValue())
                            .orElseThrow(() -> new ResourceNotFoundException("Mapped user not found: " + entry.getValue()));
                    if (mappedUser.getRole() != Role.PLAYER) {
                        throw new BadRequestException("Mapped user must have PLAYER role: " + entry.getValue());
                    }
                    state.getPlayerMappings().put(entry.getKey(), entry.getValue());
                }
            }
        }

        List<String> stillUnmapped = new ArrayList<>();
        for (String username : state.getUnmappedPlayers()) {
            if (!state.getPlayerMappings().containsKey(username)) {
                stillUnmapped.add(username);
            }
        }
        state.setUnmappedPlayers(stillUnmapped);
        batch.setSummaryJson(importPreviewService.serialize(state));
        importBatchRepository.save(batch);

        return importPreviewService.toPreviewResponse(batch, state);
    }
}
