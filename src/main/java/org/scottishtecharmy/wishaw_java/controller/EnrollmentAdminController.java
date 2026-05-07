package org.scottishtecharmy.wishaw_java.controller;

import jakarta.validation.Valid;
import org.scottishtecharmy.wishaw_java.dto.request.CreateEnrollmentRequest;
import org.scottishtecharmy.wishaw_java.dto.request.UpdateEnrollmentStatusRequest;
import org.scottishtecharmy.wishaw_java.dto.response.EnrollmentResponse;
import org.scottishtecharmy.wishaw_java.service.admin.EnrollmentAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/enrollments")
@RequiredArgsConstructor
public class EnrollmentAdminController {

    private final EnrollmentAdminService enrollmentAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentResponse createEnrollment(@Valid @RequestBody CreateEnrollmentRequest request) {
        return enrollmentAdminService.createEnrollment(request);
    }

    @PatchMapping("/{id}/status")
    public EnrollmentResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateEnrollmentStatusRequest request) {
        return enrollmentAdminService.updateStatus(id, request.getStatus());
    }
}