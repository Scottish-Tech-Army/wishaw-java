package org.scottishtecharmy.wishaw_java.group;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scottishtecharmy.wishaw_java.centre.Centre;
import org.scottishtecharmy.wishaw_java.centre.CentreRepository;
import org.scottishtecharmy.wishaw_java.group.dto.GameGroupRequest;
import org.scottishtecharmy.wishaw_java.group.dto.GameGroupResponse;
import org.scottishtecharmy.wishaw_java.user.User;
import org.scottishtecharmy.wishaw_java.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameGroupService {

    private final GameGroupRepository gameGroupRepository;
    private final CentreRepository centreRepository;
    private final UserRepository userRepository;

    public List<GameGroupResponse> findAll() {
        log.debug("Fetching all game groups");
        return gameGroupRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public GameGroupResponse findById(Long id) {
        log.debug("Fetching game group by id={}", id);
        GameGroup group = gameGroupRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("GameGroup not found: id={}", id);
                    return new IllegalArgumentException("GameGroup not found: " + id);
                });
        return toResponse(group);
    }

    public List<GameGroupResponse> findByCentre(Long centreId) {
        log.debug("Fetching game groups for centreId={}", centreId);
        return gameGroupRepository.findByCentreId(centreId).stream()
                .map(this::toResponse)
                .toList();
    }

    public GameGroupResponse create(GameGroupRequest request) {
        log.info("Creating game group: name='{}', centreId={}", request.name(), request.centreId());
        Centre centre = centreRepository.findById(request.centreId())
                .orElseThrow(() -> new IllegalArgumentException("Centre not found: " + request.centreId()));

        GameGroup group = new GameGroup();
        group.setName(request.name());
        group.setCentre(centre);
        GameGroupResponse response = toResponse(gameGroupRepository.save(group));
        log.info("GameGroup created: id={}, name='{}'", response.id(), response.name());
        return response;
    }

    public GameGroupResponse update(Long id, GameGroupRequest request) {
        log.info("Updating game group: id={}", id);
        GameGroup group = gameGroupRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("GameGroup update failed — not found: id={}", id);
                    return new IllegalArgumentException("GameGroup not found: " + id);
                });

        Centre centre = centreRepository.findById(request.centreId())
                .orElseThrow(() -> new IllegalArgumentException("Centre not found: " + request.centreId()));

        group.setName(request.name());
        group.setCentre(centre);
        return toResponse(gameGroupRepository.save(group));
    }

    public GameGroupResponse addMember(Long groupId, Long userId) {
        log.info("Adding member: userId={} to groupId={}", userId, groupId);
        GameGroup group = gameGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("GameGroup not found: " + groupId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        group.getMembers().add(user);
        GameGroupResponse response = toResponse(gameGroupRepository.save(group));
        log.info("Member added: userId={} to groupId={}, totalMembers={}", userId, groupId, response.members().size());
        return response;
    }

    public GameGroupResponse removeMember(Long groupId, Long userId) {
        log.info("Removing member: userId={} from groupId={}", userId, groupId);
        GameGroup group = gameGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("GameGroup not found: " + groupId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        group.getMembers().remove(user);
        GameGroupResponse response = toResponse(gameGroupRepository.save(group));
        log.info("Member removed: userId={} from groupId={}, totalMembers={}", userId, groupId, response.members().size());
        return response;
    }

    public void delete(Long id) {
        log.info("Deleting game group: id={}", id);
        if (!gameGroupRepository.existsById(id)) {
            log.warn("GameGroup deletion failed — not found: id={}", id);
            throw new IllegalArgumentException("GameGroup not found: " + id);
        }
        gameGroupRepository.deleteById(id);
        log.info("GameGroup deleted: id={}", id);
    }

    private GameGroupResponse toResponse(GameGroup group) {
        List<GameGroupResponse.MemberSummary> members = group.getMembers().stream()
                .map(u -> new GameGroupResponse.MemberSummary(u.getId(), u.getUsername(), u.getDisplayName(), u.getDob()))
                .toList();

        return new GameGroupResponse(
                group.getId(),
                group.getName(),
                group.getCentre() != null ? group.getCentre().getId() : null,
                group.getCentre() != null ? group.getCentre().getName() : null,
                members,
                group.getCreatedAt()
        );
    }
}
