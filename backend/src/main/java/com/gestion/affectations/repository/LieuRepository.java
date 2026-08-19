package com.gestion.affectations.repository;

import com.gestion.affectations.domain.Lieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LieuRepository extends JpaRepository<Lieu, Long> {
    Optional<Lieu> findByNom(String nom);
}
