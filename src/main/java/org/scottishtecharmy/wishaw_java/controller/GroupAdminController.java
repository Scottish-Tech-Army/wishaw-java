package org.scottishtecharmy.wishaw_java.controller;

import jakarta.validation.Valid;
import org.scottishtecharmy.wishaw_java.dto.request.CreateGroupRequest;
import org.scottishtecharmy.wishaw_java.dto.response.GroupResponse;
import org.scottishtecharmy.wishaw_java.service.admin.GroupAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/groups")
@RequiredArgsConstructor
public class GroupAdminController {

    private final GroupAdminService groupAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupResponse createGroup(@Valid @RequestBody CreateGroupRequest request) {
        return groupAdminService.createGroup(request);
    }

    @GetMapping
    public List<GroupResponse> listGroups() {
        return groupAdminService.listGroups();
    }

    @PutMapping("/{id}")
    public GroupResponse updateGroup(@PathVariable Long id, @Valid @RequestBody CreateGroupRequest request) {
        return groupAdminService.updateGroup(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@PathVariable Long id) {
        groupAdminService.deleteGroup(id);
    }
}