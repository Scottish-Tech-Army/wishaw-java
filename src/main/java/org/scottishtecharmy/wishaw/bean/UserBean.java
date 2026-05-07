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
public class UserBean implements Serializable {

    @Autowired
    private UserService userService;

    @Autowired
    private CentreService centreService;

    private List<User> users;
    private User selectedUser;
    private User newUser;
    private String selectedRoleName;
    private Long selectedCentreId;
    private List<Centre> centres;
    private String newPassword;

    @PostConstruct
    public void init() {
        users = userService.findAll();
        newUser = new User();
        centres = centreService.findAll();
    }

    public void saveUser() {
        try {
            userService.save(newUser);
            if (selectedRoleName != null && selectedCentreId != null) {
                Centre centre = centreService.findById(selectedCentreId);
                userService.assignRole(newUser, centre, selectedRoleName);
            }
            init();
            newUser = new User();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "User created successfully.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to create user: " + e.getMessage());
        }
    }

    public void updateUser() {
        try {
            userService.update(selectedUser);
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "User updated successfully.");
            init();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update user: " + e.getMessage());
        }
    }

    public void deleteUser() {
        try {
            userService.delete(selectedUser.getId());
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "User deleted.");
            init();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete user: " + e.getMessage());
        }
    }

    public void changePassword() {
        try {
            userService.changePassword(selectedUser, newPassword);
            newPassword = null;
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Password changed.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to change password: " + e.getMessage());
        }
    }

    public void prepareNew() {
        newUser = new User();
        selectedRoleName = null;
        selectedCentreId = null;
    }

    public void prepareEdit(User user) {
        this.selectedUser = user;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public String[] getRoleNames() {
        return new String[]{"centre-admin", "coach", "parent", "player"};
    }
}
