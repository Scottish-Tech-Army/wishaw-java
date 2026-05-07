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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@ViewScoped
@Data
public class BadgeBean implements Serializable {

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private CentreService centreService;

    private List<Badge> badges;
    private List<SubBadge> subBadges;
    private List<Skill> skills;
    private List<Centre> centres;
    private Badge selectedBadge;
    private Badge newBadge;
    private SubBadge selectedSubBadge;
    private SubBadge newSubBadge;
    private Skill newSkill;
    private Long newBadgeCentreId;
    private Long editBadgeCentreId;
    private Long filterBadgeId;
    private Map<Long, String> subBadgeBadgeMap;

    @PostConstruct
    public void init() {
        badges = badgeService.findAll();
        subBadges = badgeService.findAllSubBadges();
        skills = badgeService.findAllSkills();
        centres = centreService.findAll();
        newBadge = new Badge();
        newSubBadge = new SubBadge();
        newSkill = new Skill();
        newBadgeCentreId = null;
        subBadgeBadgeMap = new HashMap<>();
        for (Badge b : badges) {
            for (SubBadge sb : b.getSubBadges()) {
                subBadgeBadgeMap.put(sb.getId(), b.getName());
            }
        }
    }

    public String getParentBadgeName(SubBadge sb) {
        return subBadgeBadgeMap.getOrDefault(sb.getId(), "Unassigned");
    }

    public List<SubBadge> getFilteredSubBadges() {
        if (filterBadgeId == null) return subBadges;
        return badges.stream()
            .filter(b -> b.getId().equals(filterBadgeId))
            .findFirst()
            .map(Badge::getSubBadges)
            .orElse(subBadges);
    }

    public void saveBadge() {
        try {
            if (newBadgeCentreId != null) {
                newBadge.setCentre(centreService.findById(newBadgeCentreId));
            }
            badgeService.save(newBadge);
            newBadge = new Badge();
            newBadgeCentreId = null;
            init();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Badge saved.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void updateBadge() {
        try {
            selectedBadge.setCentre(editBadgeCentreId != null ? centreService.findById(editBadgeCentreId) : null);
            badgeService.save(selectedBadge);
            init();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Badge updated.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void prepareDeleteBadge(Badge badge) {
        this.selectedBadge = badge;
    }

    public void deleteBadge() {
        try {
            badgeService.delete(selectedBadge.getId());
            init();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Badge deleted.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void saveSubBadge() {
        try {
            badgeService.saveSubBadge(newSubBadge);
            newSubBadge = new SubBadge();
            init();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Challenge (Sub-badge) saved.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void prepareDeleteSubBadge(SubBadge subBadge) {
        this.selectedSubBadge = subBadge;
    }
    
    public void updateSubBadge() {
        try {
            badgeService.updateSubBadge(selectedSubBadge);
            init();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Challenge updated.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void deleteSubBadge() {
        try {
            badgeService.deleteSubBadge(selectedSubBadge.getId());
            init();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Challenge deleted.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void saveSkill() {
        try {
            badgeService.saveSkill(newSkill);
            newSkill = new Skill();
            init();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Skill saved.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void prepareNewBadge() {
        newBadge = new Badge();
    }

    public void prepareEditBadge(Badge badge) {
        this.selectedBadge = badge;
        this.editBadgeCentreId = badge.getCentre() != null ? badge.getCentre().getId() : null;
    }

    public void prepareNewSubBadge() {
        newSubBadge = new SubBadge();
    }

    public void prepareEditSubBadge(SubBadge subBadge) {
        this.selectedSubBadge = subBadge;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}
