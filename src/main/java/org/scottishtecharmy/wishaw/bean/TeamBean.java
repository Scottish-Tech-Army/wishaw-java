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
public class TeamBean implements Serializable {

    @Autowired
    private TeamService teamService;

    @Autowired
    private CentreService centreService;

    private List<Team> teams;
    private Team selectedTeam;
    private Team newTeam;
    private List<Centre> centres;

    @PostConstruct
    public void init() {
        teams = teamService.findAll();
        centres = centreService.findAll();
        newTeam = new Team();
    }

    public void saveTeam() {
        try {
            teamService.save(newTeam);
            newTeam = new Team();
            init();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Team saved.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void updateTeam() {
        try {
            teamService.save(selectedTeam);
            init();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Team updated.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void deleteTeam() {
        try {
            teamService.delete(selectedTeam.getId());
            init();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Team deleted.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void prepareNew() {
        newTeam = new Team();
    }

    public void prepareEdit(Team team) {
        this.selectedTeam = team;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}
