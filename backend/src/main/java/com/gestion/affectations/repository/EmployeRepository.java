package com.gestion.affectations.repository;

import com.gestion.affectations.domain.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {
    Optional<Employe> findByMatricule(String matricule);
    Optional<Employe> findByEmail(String email);
}
