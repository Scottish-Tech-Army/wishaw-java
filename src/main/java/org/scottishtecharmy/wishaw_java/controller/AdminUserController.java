package org.scottishtecharmy.wishaw_java.controller;

import jakarta.validation.Valid;
import org.scottishtecharmy.wishaw_java.dto.request.CreateUserRequest;
import org.scottishtecharmy.wishaw_java.dto.request.LinkParentRequest;
import org.scottishtecharmy.wishaw_java.dto.request.UpdateActiveStatusRequest;
import org.scottishtecharmy.wishaw_java.dto.request.UpdateUserRequest;
import org.scottishtecharmy.wishaw_java.dto.response.ParentLinkResponse;
import org.scottishtecharmy.wishaw_java.dto.response.UserSummaryResponse;
import org.scottishtecharmy.wishaw_java.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserSummaryResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return adminUserService.createUser(request);
    }

    @GetMapping("/users")
    public List<UserSummaryResponse> listUsers() {
        return adminUserService.listUsers();
    }

    @GetMapping("/users/{id}")
    public UserSummaryResponse getUser(@PathVariable Long id) {
        return adminUserService.getUser(id);
    }

    @PutMapping("/users/{id}")
    public UserSummaryResponse updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return adminUserService.updateUser(id, request);
    }

    @PatchMapping("/users/{id}/status")
    public UserSummaryResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateActiveStatusRequest request) {
        return adminUserService.updateUserStatus(id, request.getActive());
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
    }

    @PostMapping("/parents/link-player")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void linkParent(@Valid @RequestBody LinkParentRequest request) {
        adminUserService.linkParentToPlayer(request);
    }

    @GetMapping("/parents/{parentId}/links")
    public List<ParentLinkResponse> getParentLinks(@PathVariable Long parentId) {
        return adminUserService.getParentLinks(parentId);
    }

    @DeleteMapping("/parents/links/{linkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteParentLink(@PathVariable Long linkId) {
        adminUserService.deleteParentLink(linkId);
    }
}