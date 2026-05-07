package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUsername(String username);
    Optional<Student> findByEmail(String email);
    List<Student> findByTeamId(Long teamId);
    List<Student> findAllByOrderByXpDesc();
    List<Student> findByCentreId(Long centreId);
    long countByRole(String role);
}
