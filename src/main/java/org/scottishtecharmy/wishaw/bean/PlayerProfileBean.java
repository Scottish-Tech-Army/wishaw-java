package org.scottishtecharmy.wishaw.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.servlet.http.Part;
import lombok.Data;
import org.scottishtecharmy.wishaw.entity.*;
import org.scottishtecharmy.wishaw.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

@Component
@ViewScoped
@Data
public class PlayerProfileBean implements Serializable {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private UserService userService;

    @Autowired
    private BadgeService badgeService;

    private Player currentPlayer;
    private User currentUser;
    private List<PlayerBadgeDetail> playerBadges;
    private List<PlayerLevelDetail> playerLevels;
    private List<Badge> availableBadges;
    private Badge selectedBadge;
    private SubBadge selectedSubBadge;
    private Part evidencePart;
    private String evidenceUrl;

    @PostConstruct
    public void init() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            currentUser = userService.findByUsername(auth.getName());
            if (currentUser != null) {
                currentPlayer = playerService.findByUser(currentUser);
                if (currentPlayer != null) {
                    refreshData();
                }
            }
        }
    }

    private void refreshData() {
        playerBadges = playerService.findPlayerBadges(currentPlayer);
        playerLevels = playerService.findPlayerLevels(currentPlayer);
        availableBadges = badgeService.findAll();
    }

    public void submitEvidence() {
        if (currentPlayer == null || selectedBadge == null || selectedSubBadge == null) {
            addMessage(FacesMessage.SEVERITY_WARN, "Warning", "Please select a badge and challenge.");
            return;
        }
        try {
            byte[] evidenceData = null;
            if (evidencePart != null) {
                try (InputStream is = evidencePart.getInputStream()) {
                    evidenceData = is.readAllBytes();
                }
            }
            playerService.submitEvidence(currentPlayer, selectedBadge, selectedSubBadge, evidenceData, evidenceUrl);
            evidenceUrl = null;
            evidencePart = null;
            refreshData();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Evidence submitted for review.");
        } catch (IOException e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to upload evidence.");
        }
    }

    public int getBadgeXpForBadge(Badge badge) {
        if (playerLevels == null) return 0;
        return playerLevels.stream()
                .filter(pl -> pl.getBadge().getId().equals(badge.getId()))
                .mapToInt(PlayerLevelDetail::getBadgeXp)
                .findFirst().orElse(0);
    }

    public String getLevelForBadge(Badge badge) {
        if (playerLevels == null) return "Bronze";
        return playerLevels.stream()
                .filter(pl -> pl.getBadge().getId().equals(badge.getId()))
                .map(pl -> pl.getLevel().getName())
                .findFirst().orElse("Bronze");
    }

    public boolean isChallengeCompleted(SubBadge subBadge) {
        if (playerBadges == null) return false;
        return playerBadges.stream()
                .anyMatch(pb -> pb.getSubBadge().getId().equals(subBadge.getId()) && pb.isApproved());
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}
