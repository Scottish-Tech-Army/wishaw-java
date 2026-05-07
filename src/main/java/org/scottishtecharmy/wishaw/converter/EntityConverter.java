package org.scottishtecharmy.wishaw.converter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.inject.Named;
import org.scottishtecharmy.wishaw.repository.*;
import jakarta.inject.Inject;

@Named("entityConverter")
@ApplicationScoped
public class EntityConverter implements Converter<Object> {
    @Inject private BadgeRepository badgeRepository;
    @Inject private SubBadgeRepository subBadgeRepository;
    @Inject private PlayerRepository playerRepository;
    @Inject private TeamRepository teamRepository;
    @Inject private UserRepository userRepository;
    @Inject private ModuleRepository moduleRepository;
    @Inject private LevelRepository levelRepository;
    @Inject private SkillRepository skillRepository;
    @Inject private RoleRepository roleRepository;
    @Inject private CentreRepository centreRepository;
    @Inject private AgeGroupRepository ageGroupRepository;
    @Inject private PlayerBadgeDetailRepository playerBadgeDetailRepository;
    @Inject private PlayerLevelDetailRepository playerLevelDetailRepository;
    @Inject private LegacyPointRepository legacyPointRepository;
    @Inject private UserRoleRepository userRoleRepository;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        String entityClassName = (String) component.getAttributes().get("entityClass");
        if (entityClassName == null) {
            throw new IllegalArgumentException("Missing 'entityClass' attribute on component for conversion");
        }
        try {
            Long id = Long.valueOf(value);
            switch (entityClassName) {
                case "org.scottishtecharmy.wishaw.entity.Badge":
                    return badgeRepository.findById(id).orElse(null);
                case "org.scottishtecharmy.wishaw.entity.SubBadge":
                    return subBadgeRepository.findById(id).orElse(null);
                case "org.scottishtecharmy.wishaw.entity.Player":
                    return playerRepository.findById(id).orElse(null);
                case "org.scottishtecharmy.wishaw.entity.Team":
                    return teamRepository.findById(id).orElse(null);
                case "org.scottishtecharmy.wishaw.entity.User":
                    return userRepository.findById(id).orElse(null);
                case "org.scottishtecharmy.wishaw.entity.Module":
                    return moduleRepository.findById(id).orElse(null);
                case "org.scottishtecharmy.wishaw.entity.Level":
                    return levelRepository.findById(id).orElse(null);
                case "org.scottishtecharmy.wishaw.entity.Skill":
                    return skillRepository.findById(id).orElse(null);
                case "org.scottishtecharmy.wishaw.entity.Role":
                    return roleRepository.findById(id).orElse(null);
                case "org.scottishtecharmy.wishaw.entity.Centre":
                    return centreRepository.findById(id).orElse(null);
                case "org.scottishtecharmy.wishaw.entity.AgeGroup":
                    return ageGroupRepository.findById(id).orElse(null);
                case "org.scottishtecharmy.wishaw.entity.PlayerBadgeDetail":
                    return playerBadgeDetailRepository.findById(id).orElse(null);
                case "org.scottishtecharmy.wishaw.entity.PlayerLevelDetail":
                    return playerLevelDetailRepository.findById(id).orElse(null);
                case "org.scottishtecharmy.wishaw.entity.LegacyPoint":
                    return legacyPointRepository.findById(id).orElse(null);
                case "org.scottishtecharmy.wishaw.entity.UserRole":
                    return userRoleRepository.findById(id).orElse(null);
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        
        if (value == null) {
            return "";
        }
        try {
            // Assumes all entities have getId() method
            return String.valueOf(value.getClass().getMethod("getId").invoke(value));
        } catch (Exception e) {
            return value.toString();
        }
    }
}
