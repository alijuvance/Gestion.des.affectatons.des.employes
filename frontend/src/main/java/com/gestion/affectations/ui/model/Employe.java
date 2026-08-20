package com.gestion.affectations.ui.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Employe {
    private Long id;
    private String matricule;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String fonction;
    private LocalDate dateEmbauche;
    private String lieuActuel;
}
