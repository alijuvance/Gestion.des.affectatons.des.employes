package com.gestion.affectations.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "lieux")
@SQLDelete(sql = "UPDATE lieux SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
public class Lieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String adresse;

    @Column(nullable = false)
    private String ville;

    @Column(name = "capacite_max", nullable = false)
    private Integer capaciteMax;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
