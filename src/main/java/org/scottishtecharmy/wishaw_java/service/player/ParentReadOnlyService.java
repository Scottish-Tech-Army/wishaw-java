package org.scottishtecharmy.wishaw_java.service.player;

import org.scottishtecharmy.wishaw_java.dto.response.BadgeProgressResponse;
import org.scottishtecharmy.wishaw_java.dto.response.PlayerProfileResponse;
import org.scottishtecharmy.wishaw_java.dto.response.UserSummaryResponse;
import org.scottishtecharmy.wishaw_java.entity.ParentLink;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.enums.Role;
import org.scottishtecharmy.wishaw_java.exception.UnauthorizedException;
import org.scottishtecharmy.wishaw_java.mapper.DtoMapper;
import org.scottishtecharmy.wishaw_java.repository.ParentLinkRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentReadOnlyService {

    private final UserAccountRepository userAccountRepository;
    private final ParentLinkRepository parentLinkRepository;
    private final ProgressService progressService;

    public List<UserSummaryResponse> getLinkedPlayers(String parentUsername) {
        UserAccount parent = getParent(parentUsername);
        return parentLinkRepository.findByParentUserId(parent.getId()).stream()
                .map(ParentLink::getPlayerUser)
                .map(DtoMapper::toUserSummary)
                .toList();
    }

    public PlayerProfileResponse getLinkedPlayerProfile(String parentUsername, Long playerId) {
        verifyLink(parentUsername, playerId);
        return progressService.getPlayerProfile(playerId);
    }

    public List<BadgeProgressResponse> getLinkedPlayerProgress(String parentUsername, Long playerId) {
        verifyLink(parentUsername, playerId);
        return progressService.getPlayerProgress(playerId);
    }

    private void verifyLink(String parentUsername, Long playerId) {
        UserAccount parent = getParent(parentUsername);
        if (!parentLinkRepository.existsByParentUserIdAndPlayerUserId(parent.getId(), playerId)) {
            throw new UnauthorizedException("Parent account is not linked to this player");
        }
    }

    private UserAccount getParent(String parentUsername) {
        UserAccount parent = userAccountRepository.findByUsername(parentUsername)
                .orElseThrow(() -> new UnauthorizedException("Parent account not found"));
        if (parent.getRole() != Role.PARENT) {
            throw new UnauthorizedException("Authenticated user is not a parent account");
        }
        return parent;
    }
}