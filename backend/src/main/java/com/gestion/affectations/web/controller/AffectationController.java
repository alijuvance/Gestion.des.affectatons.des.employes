package com.gestion.affectations.web.controller;

import com.gestion.affectations.service.AffectationService;
import com.gestion.affectations.web.dto.AffectationDTO;
import com.gestion.affectations.web.dto.AffectationRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/affectations")
@RequiredArgsConstructor
public class AffectationController {

    private final AffectationService affectationService;

    @GetMapping
    public ResponseEntity<List<AffectationDTO>> getAllAffectations() {
        return ResponseEntity.ok(affectationService.getAllAffectations());
    }

    @GetMapping("/employe/{employeId}")
    public ResponseEntity<List<AffectationDTO>> getAffectationsByEmploye(@PathVariable Long employeId) {
        return ResponseEntity.ok(affectationService.getAffectationsByEmploye(employeId));
    }

    @PostMapping
    public ResponseEntity<AffectationDTO> createAffectation(@Valid @RequestBody AffectationRequestDTO requestDTO) {
        AffectationDTO created = affectationService.createAffectation(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}/cloturer")
    public ResponseEntity<Void> cloturerAffectation(@PathVariable Long id) {
        affectationService.cloturerAffectation(id);
        return ResponseEntity.noContent().build();
    }
}
