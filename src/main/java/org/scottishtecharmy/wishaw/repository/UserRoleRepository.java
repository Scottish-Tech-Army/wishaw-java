package org.scottishtecharmy.wishaw.repository;

import org.scottishtecharmy.wishaw.entity.UserRole;
import org.scottishtecharmy.wishaw.entity.User;
import org.scottishtecharmy.wishaw.entity.Centre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUser(User user);
    List<UserRole> findByCentre(Centre centre);
    List<UserRole> findByUserAndCentre(User user, Centre centre);
    void deleteByUser(User user);
}
