package com.gestion.affectations.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LieuDTO {
    private Long id;
    
    @NotBlank(message = "Le nom du lieu est obligatoire")
    private String nom;
    
    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;
    
    @NotBlank(message = "La ville est obligatoire")
    private String ville;
    
    @NotNull(message = "La capacité est obligatoire")
    @Min(value = 1, message = "La capacité doit être au moins de 1")
    private Integer capaciteMax;
}
