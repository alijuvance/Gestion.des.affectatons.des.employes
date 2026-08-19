package com.gestion.affectations.web.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AffectationDTO {
    private Long id;
    private EmployeDTO employe;
    private LieuDTO lieu;
    private LocalDate dateDebut;
    private LocalDate dateFin;
}
