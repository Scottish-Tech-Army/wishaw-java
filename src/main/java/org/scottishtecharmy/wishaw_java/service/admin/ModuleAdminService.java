package org.scottishtecharmy.wishaw_java.service.admin;

import org.scottishtecharmy.wishaw_java.dto.request.CreateChallengeRequest;
import org.scottishtecharmy.wishaw_java.dto.request.CreateModuleRequest;
import org.scottishtecharmy.wishaw_java.dto.request.CreateScheduleItemRequest;
import org.scottishtecharmy.wishaw_java.dto.response.ModuleDetailResponse;
import org.scottishtecharmy.wishaw_java.dto.response.ModuleSummaryResponse;
import org.scottishtecharmy.wishaw_java.entity.BadgeCategory;
import org.scottishtecharmy.wishaw_java.entity.Challenge;
import org.scottishtecharmy.wishaw_java.entity.Module;
import org.scottishtecharmy.wishaw_java.entity.ModuleScheduleItem;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.exception.DuplicateResourceException;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.mapper.DtoMapper;
import org.scottishtecharmy.wishaw_java.repository.BadgeCategoryRepository;
import org.scottishtecharmy.wishaw_java.repository.ChallengeAwardRepository;
import org.scottishtecharmy.wishaw_java.repository.ChallengeRepository;
import org.scottishtecharmy.wishaw_java.repository.ChallengeSkillTagRepository;
import org.scottishtecharmy.wishaw_java.repository.ModuleRepository;
import org.scottishtecharmy.wishaw_java.repository.ModuleScheduleItemRepository;
import org.scottishtecharmy.wishaw_java.repository.PlayerModuleEnrollmentRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ModuleAdminService {

    private final ModuleRepository moduleRepository;
    private final ChallengeRepository challengeRepository;
    private final ModuleScheduleItemRepository moduleScheduleItemRepository;
    private final BadgeCategoryRepository badgeCategoryRepository;
    private final UserAccountRepository userAccountRepository;
    private final ChallengeAwardRepository challengeAwardRepository;
    private final ChallengeSkillTagRepository challengeSkillTagRepository;
    private final PlayerModuleEnrollmentRepository playerModuleEnrollmentRepository;

    public ModuleSummaryResponse createModule(CreateModuleRequest request, String createdByUsername) {
        Module module = new Module();
        module.setName(request.getName());
        module.setGameName(request.getGameName());
        module.setDescription(request.getDescription());
        module.setCreatedBy(resolveUser(createdByUsername));
        module.setApproved(true);
        return DtoMapper.toModuleSummary(moduleRepository.save(module));
    }

    @Transactional(readOnly = true)
    public List<ModuleSummaryResponse> listModules() {
        return moduleRepository.findAll().stream()
                .map(DtoMapper::toModuleSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public ModuleDetailResponse getModule(Long moduleId) {
        Module module = getModuleEntity(moduleId);
        return DtoMapper.toModuleDetail(
                module,
                challengeRepository.findByModuleIdOrderByDisplayOrderAsc(moduleId),
                moduleScheduleItemRepository.findByModuleIdOrderByDisplayOrderAsc(moduleId)
        );
    }

    public ModuleSummaryResponse updateModule(Long moduleId, CreateModuleRequest request) {
        Module module = getModuleEntity(moduleId);
        module.setName(request.getName());
        module.setGameName(request.getGameName());
        module.setDescription(request.getDescription());
        return DtoMapper.toModuleSummary(moduleRepository.save(module));
    }

    public void deleteModule(Long moduleId) {
        getModuleEntity(moduleId); // validates existence

        // Nullify module reference on challenge awards (awards are preserved, module ref cleared)
        challengeAwardRepository.nullifyModuleId(moduleId);

        // Delete enrollments referencing this module
        playerModuleEnrollmentRepository.deleteByModuleId(moduleId);

        // Delete schedule items (which may reference challenges in this module)
        moduleScheduleItemRepository.deleteAll(
                moduleScheduleItemRepository.findByModuleIdOrderByDisplayOrderAsc(moduleId));

        // Delete each challenge and its dependents
        List<Challenge> challenges = challengeRepository.findByModuleIdOrderByDisplayOrderAsc(moduleId);
        for (Challenge challenge : challenges) {
            deleteChallengeInternal(challenge);
        }

        moduleRepository.deleteById(moduleId);
    }

    public ModuleDetailResponse createChallenge(Long moduleId, CreateChallengeRequest request) {
        Module module = getModuleEntity(moduleId);
        if (challengeRepository.existsByModuleIdAndName(moduleId, request.getName())) {
            throw new DuplicateResourceException("Challenge name must be unique within a module");
        }

        Challenge challenge = new Challenge();
        challenge.setModule(module);
        applyChallenge(challenge, request);
        challengeRepository.save(challenge);
        return getModule(moduleId);
    }

    public ModuleDetailResponse updateChallenge(Long challengeId, CreateChallengeRequest request) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found: " + challengeId));
        applyChallenge(challenge, request);
        challengeRepository.save(challenge);
        return getModule(challenge.getModule().getId());
    }

    public ModuleDetailResponse deleteChallenge(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found: " + challengeId));
        Long moduleId = challenge.getModule().getId();
        deleteChallengeInternal(challenge);
        return getModule(moduleId);
    }

    public ModuleDetailResponse createScheduleItem(Long moduleId, CreateScheduleItemRequest request) {
        ModuleScheduleItem item = new ModuleScheduleItem();
        item.setModule(getModuleEntity(moduleId));
        applyScheduleItem(item, request);
        moduleScheduleItemRepository.save(item);
        return getModule(moduleId);
    }

    public ModuleDetailResponse updateScheduleItem(Long scheduleItemId, CreateScheduleItemRequest request) {
        ModuleScheduleItem item = moduleScheduleItemRepository.findById(scheduleItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule item not found: " + scheduleItemId));
        applyScheduleItem(item, request);
        moduleScheduleItemRepository.save(item);
        return getModule(item.getModule().getId());
    }

    private void deleteChallengeInternal(Challenge challenge) {
        // Nullify challenge reference on awards (awards are preserved, challenge ref cleared)
        challengeAwardRepository.nullifyChallengeId(challenge.getId());

        // Nullify linked challenge on schedule items
        moduleScheduleItemRepository.findByModuleIdOrderByDisplayOrderAsc(challenge.getModule().getId())
                .forEach(item -> {
                    if (item.getLinkedChallenge() != null && item.getLinkedChallenge().getId().equals(challenge.getId())) {
                        item.setLinkedChallenge(null);
                        moduleScheduleItemRepository.save(item);
                    }
                });

        // Delete skill tags
        challengeSkillTagRepository.deleteAll(
                challengeSkillTagRepository.findByChallengeId(challenge.getId()));

        challengeRepository.delete(challenge);
    }

    private void applyChallenge(Challenge challenge, CreateChallengeRequest request) {
        BadgeCategory badgeCategory = badgeCategoryRepository.findById(request.getBadgeCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Badge category not found: " + request.getBadgeCategoryId()));

        challenge.setBadgeCategory(badgeCategory);
        challenge.setName(request.getName());
        challenge.setDescription(request.getDescription());
        challenge.setPoints(request.getPoints());
        challenge.setDisplayOrder(request.getDisplayOrder());
    }

    private void applyScheduleItem(ModuleScheduleItem item, CreateScheduleItemRequest request) {
        item.setWeekNumber(request.getWeekNumber());
        item.setSessionFocus(request.getSessionFocus());
        item.setSessionPlanUrl(request.getSessionPlanUrl());
        item.setSessionSlidesUrl(request.getSessionSlidesUrl());
        item.setDisplayOrder(request.getDisplayOrder());

        if (request.getLinkedChallengeId() != null) {
            Challenge linkedChallenge = challengeRepository.findById(request.getLinkedChallengeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Linked challenge not found: " + request.getLinkedChallengeId()));
            item.setLinkedChallenge(linkedChallenge);
        } else {
            item.setLinkedChallenge(null);
        }
    }

    private Module getModuleEntity(Long moduleId) {
        return moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found: " + moduleId));
    }

    private UserAccount resolveUser(String username) {
        return userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }
}
