package org.scottishtecharmy.wishaw.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import lombok.Data;
import org.scottishtecharmy.wishaw.entity.*;
import org.scottishtecharmy.wishaw.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
@ViewScoped
@Data
public class AgeGroupBean implements Serializable {

    @Autowired
    private AgeGroupService ageGroupService;

    private List<AgeGroup> ageGroups;
    private AgeGroup selectedAgeGroup;
    private AgeGroup newAgeGroup;

    @PostConstruct
    public void init() {
        ageGroups = ageGroupService.findAll();
        newAgeGroup = new AgeGroup();
    }

    public void saveAgeGroup() {
        try {
            ageGroupService.save(newAgeGroup);
            newAgeGroup = new AgeGroup();
            init();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Age Group saved.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void updateAgeGroup() {
        try {
            ageGroupService.save(selectedAgeGroup);
            init();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Age Group updated.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void deleteAgeGroup() {
        try {
            ageGroupService.delete(selectedAgeGroup.getId());
            init();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Age Group deleted.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void prepareNew() {
        newAgeGroup = new AgeGroup();
    }

    public void prepareEdit(AgeGroup ageGroup) {
        this.selectedAgeGroup = ageGroup;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}
