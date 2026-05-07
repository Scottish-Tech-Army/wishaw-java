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
public class PlayerBean implements Serializable {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private UserService userService;

    @Autowired
    private CentreService centreService;

    @Autowired
    private AgeGroupService ageGroupService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private BadgeService badgeService;

    private List<Player> players;
    private Player selectedPlayer;
    private Player newPlayer;
    private List<User> users;
    private List<AgeGroup> ageGroups;
    private List<Module> modules;
    private List<Team> teams;
    private List<Centre> centres;
    private List<PlayerBadgeDetail> selectedPlayerBadges;
    private List<PlayerLevelDetail> selectedPlayerLevels;
    private List<PlayerBadgeDetail> pendingApprovals;
    private String coachNotes;
    private List<Badge> badges;
    private Badge selectedBadge;
    private SubBadge selectedSubBadge;

    @PostConstruct
    public void init() {
        players = playerService.findAll();
        users = userService.findAll();
        ageGroups = ageGroupService.findAll();
        modules = moduleService.findAll();
        teams = teamService.findAll();
        centres = centreService.findAll();
        badges = badgeService.findAll();
        newPlayer = new Player();

        System.out.println("Player number: " + players.size());
    }

    public void savePlayer() {
        try {
            playerService.save(newPlayer);
            newPlayer = new Player();
            init();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Player created.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void updatePlayer() {
        try {
            playerService.save(selectedPlayer);
            init();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Player updated.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void deletePlayer(Player player) {
        try {
            playerService.delete(player.getId());
            init();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Player deleted.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void selectPlayer(Player player) {
        this.selectedPlayer = player;
        this.selectedPlayerBadges = playerService.findPlayerBadges(player);
        this.selectedPlayerLevels = playerService.findPlayerLevels(player);
        this.pendingApprovals = playerService.findPendingApprovals(player);
    }

    public void approveChallenge(PlayerBadgeDetail detail) {
        try {
            playerService.approveChallenge(detail, coachNotes);
            if (selectedPlayer != null) {
                selectPlayer(selectedPlayer);
            }
            coachNotes = null;
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Challenge approved.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void rejectChallenge(PlayerBadgeDetail detail) {
        try {
            playerService.rejectChallenge(detail, coachNotes);
            if (selectedPlayer != null) {
                selectPlayer(selectedPlayer);
            }
            coachNotes = null;
            addMessage(FacesMessage.SEVERITY_INFO, "Info", "Challenge rejected.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void prepareNewPlayer() {
        newPlayer = new Player();
    }

    public void awardSubBadge() {
        try {
            if (selectedPlayer == null || selectedBadge == null || selectedSubBadge == null) {
                addMessage(FacesMessage.SEVERITY_WARN, "Warning", "Please select a player, badge, and sub-badge.");
                return;
            }
            playerService.submitEvidence(selectedPlayer, selectedBadge, selectedSubBadge, null, null);
            // Find the newly created pending detail and approve it immediately
            List<PlayerBadgeDetail> pending = playerService.findPendingApprovals(selectedPlayer);
            for (PlayerBadgeDetail pd : pending) {
                if (pd.getSubBadge().getId().equals(selectedSubBadge.getId())
                        && pd.getBadge().getId().equals(selectedBadge.getId())) {
                    playerService.approveChallenge(pd, coachNotes);
                    break;
                }
            }
            selectPlayer(selectedPlayer);
            selectedBadge = null;
            selectedSubBadge = null;
            coachNotes = null;
            addMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Sub-badge awarded to " + selectedPlayer.getUser().getFirstName() + ".");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}
