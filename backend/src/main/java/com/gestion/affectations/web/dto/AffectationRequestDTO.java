package com.gestion.affectations.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AffectationRequestDTO {
    @NotNull(message = "L'ID de l'employé est obligatoire")
    private Long employeId;
    
    @NotNull(message = "L'ID du lieu est obligatoire")
    private Long lieuId;
    
    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;
}
