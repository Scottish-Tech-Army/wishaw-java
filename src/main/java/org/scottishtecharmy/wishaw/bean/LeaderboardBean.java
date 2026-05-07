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
public class LeaderboardBean implements Serializable {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private CentreService centreService;

    @Autowired
    private UserService userService;

    private List<Player> leaderboard;
    private List<Centre> centres;
    private Long selectedCentreId;

    @PostConstruct
    public void init() {
        centres = centreService.findAll();
        if (!centres.isEmpty()) {
            selectedCentreId = centres.get(0).getId();
            loadLeaderboard();
        }
    }

    public void loadLeaderboard() {
        if (selectedCentreId != null) {
            Centre centre = centreService.findById(selectedCentreId);
            leaderboard = playerService.findByCentreLeaderboard(centre);
        }
    }

    public String getLevelName(Player player) {
        List<PlayerLevelDetail> levels = playerService.findPlayerLevels(player);
        if (levels.isEmpty()) return "Bronze";
        return levels.stream()
                .mapToInt(PlayerLevelDetail::getBadgeXp)
                .sum() > 0 ? levels.get(0).getLevel().getName() : "Bronze";
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}
