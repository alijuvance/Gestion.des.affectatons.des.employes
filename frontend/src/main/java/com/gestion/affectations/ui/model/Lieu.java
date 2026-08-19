package com.gestion.affectations.ui.model;

import lombok.Data;

@Data
public class Lieu {
    private Long id;
    private String nom;
    private String adresse;
    private String ville;
    private Integer capaciteMax;
}
