package org.scottishtecharmy.wishaw_java.service;

import org.scottishtecharmy.wishaw_java.dto.CatalogueDtos;
import org.scottishtecharmy.wishaw_java.entity.LearningModule;
import org.scottishtecharmy.wishaw_java.entity.NotificationRecord;
import org.scottishtecharmy.wishaw_java.entity.SubBadge;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.entity.UserSubBadge;
import org.scottishtecharmy.wishaw_java.enums.ModuleStatus;
import org.scottishtecharmy.wishaw_java.enums.NotificationType;
import org.scottishtecharmy.wishaw_java.exception.BadRequestException;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.mapper.ApiMapper;
import org.scottishtecharmy.wishaw_java.repository.CentreRepository;
import org.scottishtecharmy.wishaw_java.repository.GameGroupRepository;
import org.scottishtecharmy.wishaw_java.repository.LearningModuleRepository;
import org.scottishtecharmy.wishaw_java.repository.MainBadgeRepository;
import org.scottishtecharmy.wishaw_java.repository.ModuleSessionItemRepository;
import org.scottishtecharmy.wishaw_java.repository.NotificationRecordRepository;
import org.scottishtecharmy.wishaw_java.repository.SubBadgeRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import org.scottishtecharmy.wishaw_java.repository.UserSubBadgeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CatalogueService {

    private final CentreRepository centreRepository;
    private final GameGroupRepository gameGroupRepository;
    private final MainBadgeRepository mainBadgeRepository;
    private final SubBadgeRepository subBadgeRepository;
    private final UserSubBadgeRepository userSubBadgeRepository;
    private final LearningModuleRepository learningModuleRepository;
    private final ModuleSessionItemRepository moduleSessionItemRepository;
    private final UserAccountRepository userAccountRepository;
    private final NotificationRecordRepository notificationRecordRepository;
    private final ApiMapper apiMapper;
    private final BadgeLevelService badgeLevelService;

    public CatalogueService(CentreRepository centreRepository,
                            GameGroupRepository gameGroupRepository,
                            MainBadgeRepository mainBadgeRepository,
                            SubBadgeRepository subBadgeRepository,
                            UserSubBadgeRepository userSubBadgeRepository,
                            LearningModuleRepository learningModuleRepository,
                            ModuleSessionItemRepository moduleSessionItemRepository,
                            UserAccountRepository userAccountRepository,
                            NotificationRecordRepository notificationRecordRepository,
                            ApiMapper apiMapper,
                            BadgeLevelService badgeLevelService) {
        this.centreRepository = centreRepository;
        this.gameGroupRepository = gameGroupRepository;
        this.mainBadgeRepository = mainBadgeRepository;
        this.subBadgeRepository = subBadgeRepository;
        this.userSubBadgeRepository = userSubBadgeRepository;
        this.learningModuleRepository = learningModuleRepository;
        this.moduleSessionItemRepository = moduleSessionItemRepository;
        this.userAccountRepository = userAccountRepository;
        this.notificationRecordRepository = notificationRecordRepository;
        this.apiMapper = apiMapper;
        this.badgeLevelService = badgeLevelService;
    }

    @Transactional(readOnly = true)
    public List<CatalogueDtos.CentreDto> getCentres() {
        return centreRepository.findAll().stream()
                .map(apiMapper::toCentreDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CatalogueDtos.GroupDto> getGroups(String centreId) {
                return (centreId == null || centreId.isBlank() ? gameGroupRepository.findAll() : gameGroupRepository.findByCentre_IdOrderByNameAsc(centreId))
                .stream()
                .map(apiMapper::toGroupDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CatalogueDtos.MainBadgeDto> getMainBadges() {
        return mainBadgeRepository.findAll().stream()
                .map(apiMapper::toMainBadgeDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CatalogueDtos.SubBadgeDto> getSubBadges(String moduleId) {
        List<SubBadge> subBadges = (moduleId == null || moduleId.isBlank())
                ? subBadgeRepository.findAll()
                                : subBadgeRepository.findByLearningModule_IdOrderByNameAsc(moduleId);
        return subBadges.stream().map(apiMapper::toSubBadgeDto).toList();
    }

    @Transactional(readOnly = true)
    public List<CatalogueDtos.UserBadgeProgressDto> getUserBadgeProgress(String userId) {
        userAccountRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
                Map<String, List<UserSubBadge>> awardsByMainBadge = userSubBadgeRepository.findByUserAccount_Id(userId).stream()
                .collect(Collectors.groupingBy(userSubBadge -> userSubBadge.getSubBadge().getMainBadge().getId()));

        return mainBadgeRepository.findAll().stream()
                .map(mainBadge -> {
                    List<UserSubBadge> awards = awardsByMainBadge.getOrDefault(mainBadge.getId(), List.of());
                    int totalPoints = awards.stream().mapToInt(item -> item.getSubBadge().getPoints()).sum();
                    List<String> earnedSubBadges = awards.stream().map(item -> item.getSubBadge().getId()).toList();
                    return new CatalogueDtos.UserBadgeProgressDto(
                            mainBadge.getId(),
                            mainBadge.getName(),
                            totalPoints,
                            badgeLevelService.resolveLabel(totalPoints),
                            earnedSubBadges
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CatalogueDtos.ModuleDto> getModules() {
        return learningModuleRepository.findAll().stream().map(this::buildModule).toList();
    }

    @Transactional(readOnly = true)
    public CatalogueDtos.ModuleDto getModule(String id) {
        LearningModule learningModule = learningModuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));
        return buildModule(learningModule);
    }

        public CatalogueDtos.ModuleDto createModule(CatalogueDtos.ModuleUpsertRequest request) {
                LearningModule learningModule = new LearningModule();
                learningModule.setId("m-" + UUID.randomUUID());
                applyModuleUpdate(learningModule, request);
                return buildModule(learningModuleRepository.save(learningModule));
        }

        public CatalogueDtos.ModuleDto updateModule(String id, CatalogueDtos.ModuleUpsertRequest request) {
                LearningModule learningModule = learningModuleRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));
                applyModuleUpdate(learningModule, request);
                return buildModule(learningModuleRepository.save(learningModule));
        }

        public Map<String, Boolean> deleteModule(String id) {
                LearningModule learningModule = learningModuleRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));

                List<String> subBadgeIds = subBadgeRepository.findByLearningModule_IdOrderByNameAsc(id).stream()
                                .map(SubBadge::getId)
                                .toList();

                moduleSessionItemRepository.deleteByLearningModule_Id(id);
                if (!subBadgeIds.isEmpty()) {
                        userSubBadgeRepository.deleteBySubBadge_IdIn(subBadgeIds);
                }
                subBadgeRepository.deleteByLearningModule_Id(id);
                learningModuleRepository.delete(learningModule);

                return Map.of("success", true);
        }

    public Map<String, Boolean> awardSubBadge(String userId, String subBadgeId) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        SubBadge subBadge = subBadgeRepository.findById(subBadgeId)
                .orElseThrow(() -> new ResourceNotFoundException("Sub badge not found"));

        userSubBadgeRepository.findByUserAccount_IdAndSubBadge_Id(userId, subBadgeId).orElseGet(() -> {
            UserSubBadge award = UserSubBadge.builder()
                    .userAccount(userAccount)
                    .subBadge(subBadge)
                    .awardedAt(Instant.now())
                    .build();
            notificationRecordRepository.save(NotificationRecord.builder()
                    .id("n-" + UUID.randomUUID())
                    .userAccount(userAccount)
                    .type(NotificationType.BADGE)
                    .title("Badge Earned!")
                    .message("You earned the " + subBadge.getName() + " sub-badge!")
                    .isRead(false)
                    .createdAt(Instant.now())
                    .linkTo("/badges")
                    .build());
            return userSubBadgeRepository.save(award);
        });

        return Map.of("success", true);
    }

        private void applyModuleUpdate(LearningModule learningModule, CatalogueDtos.ModuleUpsertRequest request) {
                learningModule.setName(requireNonBlank(request.name(), "Module name is required"));
                learningModule.setGame(requireNonBlank(request.game(), "Game is required"));
                learningModule.setDescription(requireNonBlank(request.description(), "Description is required"));

                if (request.durationWeeks() == null || request.durationWeeks() <= 0) {
                        throw new BadRequestException("Duration weeks must be greater than 0");
                }
                learningModule.setDurationWeeks(request.durationWeeks());
                learningModule.setStatus(resolveStatus(request.status(), learningModule.getStatus()));
        }

        private String requireNonBlank(String value, String message) {
                if (value == null || value.isBlank()) {
                        throw new BadRequestException(message);
                }
                return value.trim();
        }

        private ModuleStatus resolveStatus(String rawStatus, ModuleStatus currentStatus) {
                if (rawStatus == null || rawStatus.isBlank()) {
                        return currentStatus == null ? ModuleStatus.DRAFT : currentStatus;
                }

                try {
                        return ModuleStatus.valueOf(rawStatus.trim().toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException exception) {
                        throw new BadRequestException("Invalid module status");
                }
        }

    private CatalogueDtos.ModuleDto buildModule(LearningModule learningModule) {
        List<CatalogueDtos.SubBadgeDto> subBadges = subBadgeRepository.findByLearningModule_IdOrderByNameAsc(learningModule.getId()).stream()
                .map(apiMapper::toSubBadgeDto)
                .toList();
        List<CatalogueDtos.ModuleSessionDto> sessions = moduleSessionItemRepository.findByLearningModule_IdOrderByWeekNoAsc(learningModule.getId()).stream()
                .map(apiMapper::toModuleSessionDto)
                .toList();
        return apiMapper.toModuleDto(learningModule, subBadges, sessions);
    }
}
