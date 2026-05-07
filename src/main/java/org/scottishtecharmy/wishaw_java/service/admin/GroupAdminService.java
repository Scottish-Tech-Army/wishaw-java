package org.scottishtecharmy.wishaw_java.service.admin;

import org.scottishtecharmy.wishaw_java.dto.request.CreateGroupRequest;
import org.scottishtecharmy.wishaw_java.dto.response.GroupResponse;
import org.scottishtecharmy.wishaw_java.entity.Centre;
import org.scottishtecharmy.wishaw_java.entity.Group;
import org.scottishtecharmy.wishaw_java.exception.BadRequestException;
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
public class GroupAdminService {

    private final GroupRepository groupRepository;
    private final CentreRepository centreRepository;
    private final UserAccountRepository userAccountRepository;

    public GroupResponse createGroup(CreateGroupRequest request) {
        Group group = new Group();
        apply(group, request);
        return DtoMapper.toGroupResponse(groupRepository.save(group));
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> listGroups() {
        return groupRepository.findAll().stream()
                .map(DtoMapper::toGroupResponse)
                .toList();
    }

    public GroupResponse updateGroup(Long groupId, CreateGroupRequest request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found: " + groupId));
        apply(group, request);
        return DtoMapper.toGroupResponse(groupRepository.save(group));
    }

    public void deleteGroup(Long groupId) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found: " + groupId));

        if (!userAccountRepository.findByGroupId(groupId).isEmpty()) {
            throw new BadRequestException("Cannot delete group: users are still assigned to it");
        }

        groupRepository.deleteById(groupId);
    }

    private void apply(Group group, CreateGroupRequest request) {
        Centre centre = centreRepository.findById(request.getCentreId())
                .orElseThrow(() -> new ResourceNotFoundException("Centre not found: " + request.getCentreId()));

        group.setName(request.getName());
        group.setGameName(request.getGameName());
        group.setAgeBand(request.getAgeBand());
        group.setCentre(centre);
    }
}
