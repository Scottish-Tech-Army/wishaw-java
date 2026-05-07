package org.scottishtecharmy.wishaw_java.controller;

import jakarta.validation.Valid;
import org.scottishtecharmy.wishaw_java.dto.request.CreateCentreRequest;
import org.scottishtecharmy.wishaw_java.dto.response.CentreResponse;
import org.scottishtecharmy.wishaw_java.service.admin.CentreAdminService;
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
@RequestMapping("/api/v1/admin/centres")
@RequiredArgsConstructor
public class CentreAdminController {

    private final CentreAdminService centreAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CentreResponse createCentre(@Valid @RequestBody CreateCentreRequest request) {
        return centreAdminService.createCentre(request);
    }

    @GetMapping
    public List<CentreResponse> listCentres() {
        return centreAdminService.listCentres();
    }

    @PutMapping("/{id}")
    public CentreResponse updateCentre(@PathVariable Long id, @Valid @RequestBody CreateCentreRequest request) {
        return centreAdminService.updateCentre(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCentre(@PathVariable Long id) {
        centreAdminService.deleteCentre(id);
    }
}