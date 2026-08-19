package com.gestion.affectations.ui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AffectationRequest {
    private Long employeId;
    private Long lieuId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
}
