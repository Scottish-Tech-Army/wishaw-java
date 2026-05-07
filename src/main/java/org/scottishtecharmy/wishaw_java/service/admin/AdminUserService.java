package org.scottishtecharmy.wishaw_java.service.admin;

import org.scottishtecharmy.wishaw_java.dto.request.CreateUserRequest;
import org.scottishtecharmy.wishaw_java.dto.request.LinkParentRequest;
import org.scottishtecharmy.wishaw_java.dto.request.UpdateUserRequest;
import org.scottishtecharmy.wishaw_java.dto.response.ParentLinkResponse;
import org.scottishtecharmy.wishaw_java.dto.response.UserSummaryResponse;
import org.scottishtecharmy.wishaw_java.entity.Centre;
import org.scottishtecharmy.wishaw_java.entity.Group;
import org.scottishtecharmy.wishaw_java.entity.ParentLink;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.enums.Role;
import org.scottishtecharmy.wishaw_java.exception.BadRequestException;
import org.scottishtecharmy.wishaw_java.exception.DuplicateResourceException;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.mapper.DtoMapper;
import org.scottishtecharmy.wishaw_java.repository.CentreRepository;
import org.scottishtecharmy.wishaw_java.repository.ChallengeAwardRepository;
import org.scottishtecharmy.wishaw_java.repository.GroupRepository;
import org.scottishtecharmy.wishaw_java.repository.ModuleRepository;
import org.scottishtecharmy.wishaw_java.repository.ParentLinkRepository;
import org.scottishtecharmy.wishaw_java.repository.PlayerBadgeProgressRepository;
import org.scottishtecharmy.wishaw_java.repository.PlayerModuleEnrollmentRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {

    private final UserAccountRepository userAccountRepository;
    private final CentreRepository centreRepository;
    private final GroupRepository groupRepository;
    private final ParentLinkRepository parentLinkRepository;
    private final ChallengeAwardRepository challengeAwardRepository;
    private final PlayerBadgeProgressRepository playerBadgeProgressRepository;
    private final PlayerModuleEnrollmentRepository playerModuleEnrollmentRepository;
    private final ModuleRepository moduleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSummaryResponse createUser(CreateUserRequest request) {
        if (userAccountRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + request.getUsername());
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setUsername(request.getUsername());
        userAccount.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userAccount.setDisplayName(request.getDisplayName());
        userAccount.setRole(parseRole(request.getRole()));
        userAccount.setCentre(resolveCentre(request.getCentreId()));
        userAccount.setGroup(resolveGroup(request.getGroupId()));
        userAccount.setExternalRef(request.getExternalRef());

        return DtoMapper.toUserSummary(userAccountRepository.save(userAccount));
    }

    @Transactional(readOnly = true)
    public List<UserSummaryResponse> listUsers() {
        return userAccountRepository.findAll().stream()
                .map(DtoMapper::toUserSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserSummaryResponse getUser(Long userId) {
        return DtoMapper.toUserSummary(getUserAccount(userId));
    }

    public UserSummaryResponse updateUser(Long userId, UpdateUserRequest request) {
        UserAccount userAccount = getUserAccount(userId);
        if (request.getDisplayName() != null) {
            userAccount.setDisplayName(request.getDisplayName());
        }
        if (request.getRole() != null) {
            userAccount.setRole(parseRole(request.getRole()));
        }
        if (request.getCentreId() != null) {
            userAccount.setCentre(resolveCentre(request.getCentreId()));
        }
        if (request.getGroupId() != null) {
            userAccount.setGroup(resolveGroup(request.getGroupId()));
        }
        if (request.getExternalRef() != null) {
            userAccount.setExternalRef(request.getExternalRef());
        }

        return DtoMapper.toUserSummary(userAccountRepository.save(userAccount));
    }

    public UserSummaryResponse updateUserStatus(Long userId, boolean active) {
        UserAccount userAccount = getUserAccount(userId);
        userAccount.setActive(active);
        return DtoMapper.toUserSummary(userAccountRepository.save(userAccount));
    }

    public void deleteUser(Long userId) {
        getUserAccount(userId); // validates existence

        // Remove parent links where this user is a parent or player
        parentLinkRepository.deleteByParentUserIdOrPlayerUserId(userId, userId);

        // Nullify awarded_by references in challenge awards (records persist, auditor field cleared)
        challengeAwardRepository.nullifyAwardedBy(userId);

        // Remove all player-specific data
        challengeAwardRepository.deleteByPlayerId(userId);
        playerBadgeProgressRepository.deleteByPlayerId(userId);
        playerModuleEnrollmentRepository.deleteByPlayerId(userId);

        // Nullify createdBy on modules (module records persist)
        moduleRepository.nullifyCreatedBy(userId);

        userAccountRepository.deleteById(userId);
    }

    public void linkParentToPlayer(LinkParentRequest request) {
        UserAccount parentUser = getUserAccount(request.getParentUserId());
        UserAccount playerUser = getUserAccount(request.getPlayerUserId());

        if (parentUser.getRole() != Role.PARENT) {
            throw new ResourceNotFoundException("Selected parent user does not have PARENT role");
        }
        if (playerUser.getRole() != Role.PLAYER) {
            throw new ResourceNotFoundException("Selected player user does not have PLAYER role");
        }
        if (parentLinkRepository.existsByParentUserIdAndPlayerUserId(parentUser.getId(), playerUser.getId())) {
            throw new DuplicateResourceException("Parent link already exists");
        }

        ParentLink parentLink = new ParentLink();
        parentLink.setParentUser(parentUser);
        parentLink.setPlayerUser(playerUser);
        parentLink.setRelationshipLabel(request.getRelationshipLabel());
        parentLinkRepository.save(parentLink);
    }

    @Transactional(readOnly = true)
    public List<ParentLinkResponse> getParentLinks(Long parentUserId) {
        getUserAccount(parentUserId); // validates existence
        return parentLinkRepository.findByParentUserId(parentUserId).stream()
                .map(DtoMapper::toParentLinkResponse)
                .toList();
    }

    public void deleteParentLink(Long linkId) {
        if (!parentLinkRepository.existsById(linkId)) {
            throw new ResourceNotFoundException("Parent link not found: " + linkId);
        }
        parentLinkRepository.deleteById(linkId);
    }

    private UserAccount getUserAccount(Long userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private Centre resolveCentre(Long centreId) {
        if (centreId == null) {
            return null;
        }
        return centreRepository.findById(centreId)
                .orElseThrow(() -> new ResourceNotFoundException("Centre not found: " + centreId));
    }

    private Group resolveGroup(Long groupId) {
        if (groupId == null) {
            return null;
        }
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found: " + groupId));
    }

    private Role parseRole(String roleValue) {
        try {
            return Role.valueOf(roleValue.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid role value: " + roleValue);
        }
    }
}
