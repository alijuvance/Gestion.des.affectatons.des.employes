package com.gestion.affectations.web.controller;

import com.gestion.affectations.service.LieuService;
import com.gestion.affectations.web.dto.LieuDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lieux")
@RequiredArgsConstructor
public class LieuController {

    private final LieuService lieuService;

    @GetMapping
    public ResponseEntity<List<LieuDTO>> getAllLieux() {
        return ResponseEntity.ok(lieuService.getAllLieux());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LieuDTO> getLieuById(@PathVariable Long id) {
        return ResponseEntity.ok(lieuService.getLieuById(id));
    }

    @PostMapping
    public ResponseEntity<LieuDTO> createLieu(@Valid @RequestBody LieuDTO lieuDTO) {
        LieuDTO created = lieuService.createLieu(lieuDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LieuDTO> updateLieu(@PathVariable Long id, @Valid @RequestBody LieuDTO lieuDTO) {
        return ResponseEntity.ok(lieuService.updateLieu(id, lieuDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLieu(@PathVariable Long id) {
        lieuService.deleteLieu(id);
        return ResponseEntity.noContent().build();
    }
}
