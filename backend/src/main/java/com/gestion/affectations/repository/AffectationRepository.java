package com.gestion.affectations.repository;

import com.gestion.affectations.domain.Affectation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AffectationRepository extends JpaRepository<Affectation, Long> {
    List<Affectation> findByEmployeId(Long employeId);
    List<Affectation> findByLieuId(Long lieuId);
    
    // Trouver l'affectation en cours pour un employé (date_fin est null)
    Optional<Affectation> findByEmployeIdAndDateFinIsNull(Long employeId);
    
    // Trouver l'affectation active à la date d'aujourd'hui
    @org.springframework.data.jpa.repository.Query("SELECT a FROM Affectation a WHERE a.employe.id = :employeId AND a.dateDebut <= CURRENT_DATE AND (a.dateFin IS NULL OR a.dateFin >= CURRENT_DATE)")
    Optional<Affectation> findActiveByEmployeId(@org.springframework.data.repository.query.Param("employeId") Long employeId);
}
