package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
    boolean existsByUsername(String username);
    List<UserAccount> findByRole(Role role);
    List<UserAccount> findByCentreId(Long centreId);
    List<UserAccount> findByGroupId(Long groupId);
    Optional<UserAccount> findByExternalRef(String externalRef);
}
