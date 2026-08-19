package com.gestion.affectations.ui.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Affectation {
    private Long id;
    private Employe employe;
    private Lieu lieu;
    private LocalDate dateDebut;
    private LocalDate dateFin;
}
