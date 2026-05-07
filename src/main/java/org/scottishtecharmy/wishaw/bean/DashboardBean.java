package org.scottishtecharmy.wishaw.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import lombok.Data;
import org.scottishtecharmy.wishaw.entity.*;
import org.scottishtecharmy.wishaw.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@ViewScoped
@Data
public class DashboardBean implements Serializable {

    @Autowired
    private UserService userService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private CentreService centreService;

    private User currentUser;
    private Player currentPlayer;
    private String userRole;

    @PostConstruct
    public void init() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            currentUser = userService.findByUsername(auth.getName());
            if (currentUser != null) {
                currentPlayer = playerService.findByUser(currentUser);
            }
            userRole = auth.getAuthorities().stream()
                    .findFirst()
                    .map(a -> a.getAuthority().replace("ROLE_", ""))
                    .orElse("");
        }

        System.out.println("User Role: " + userRole);

    }

    public String navigateTo() {

        System.out.println("User Role: " + userRole);

        if ("centre-admin".equals(userRole)) {
            return "/admin/dashboard.xhtml?faces-redirect=true";
        } else if ("coach".equals(userRole)) {
            return "/coach/dashboard.xhtml?faces-redirect=true";
        } else if ("player".equals(userRole)) {
            return "/player/profile.xhtml?faces-redirect=true";
        } else if ("parent".equals(userRole)) {
            return "/player/profile.xhtml?faces-redirect=true";
        }
        return "/login.xhtml?faces-redirect=true";
    }

    public boolean isAdmin() {
        return "centre-admin".equals(userRole);
    }

    public boolean isCoach() {
        return "coach".equals(userRole) || isAdmin();
    }

    public boolean isPlayer() {
        return "player".equals(userRole);
    }

    public boolean isParent() {
        return "parent".equals(userRole);
    }
}
