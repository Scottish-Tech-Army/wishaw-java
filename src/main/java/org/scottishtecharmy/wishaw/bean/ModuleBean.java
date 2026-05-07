package org.scottishtecharmy.wishaw.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import lombok.Data;
import org.scottishtecharmy.wishaw.entity.*;
import org.scottishtecharmy.wishaw.entity.Module;
import org.scottishtecharmy.wishaw.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
@ViewScoped
@Data
public class ModuleBean implements Serializable {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private CentreService centreService;

    @Autowired
    private BadgeService badgeService;

    private List<Module> modules;
    private Module selectedModule;
    private Module newModule;
    private ModuleDetail newModuleDetail;
    private List<Centre> centres;
    private List<SubBadge> subBadges;

    @PostConstruct
    public void init() {
        modules = moduleService.findAll();
        centres = centreService.findAll();
        subBadges = badgeService.findAllSubBadges();
        newModule = new Module();
        newModuleDetail = new ModuleDetail();
    }

    public void saveModule() {
        try {
            moduleService.save(newModule);
            newModule = new Module();
            init();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Module saved.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void updateModule() {
        try {
            moduleService.save(selectedModule);
            init();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Module updated.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void deleteModule() {
        try {
            moduleService.delete(selectedModule.getId());
            init();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Module deleted.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void addModuleDetail() {
        if (selectedModule != null) {
            selectedModule.getModuleDetails().add(newModuleDetail);
            moduleService.save(selectedModule);
            newModuleDetail = new ModuleDetail();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Session added to module.");
        }
    }

    public void removeModuleDetail(ModuleDetail detail) {
        if (selectedModule != null) {
            selectedModule.getModuleDetails().remove(detail);
            moduleService.save(selectedModule);
        }
    }

    public void selectModule(Module module) {
        this.selectedModule = module;
        newModuleDetail = new ModuleDetail();
    }

    public void prepareNewModule() {
        newModule = new Module();
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}
