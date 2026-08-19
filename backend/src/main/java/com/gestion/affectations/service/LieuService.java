package com.gestion.affectations.service;

import com.gestion.affectations.domain.Lieu;
import com.gestion.affectations.exception.BusinessRuleException;
import com.gestion.affectations.exception.ResourceNotFoundException;
import com.gestion.affectations.repository.LieuRepository;
import com.gestion.affectations.web.dto.LieuDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LieuService {
    
    private final LieuRepository lieuRepository;
    
    public List<LieuDTO> getAllLieux() {
        return lieuRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public LieuDTO getLieuById(Long id) {
        Lieu lieu = lieuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lieu introuvable avec l'ID : " + id));
        return mapToDTO(lieu);
    }
    
    public LieuDTO createLieu(LieuDTO dto) {
        if (lieuRepository.findByNom(dto.getNom()).isPresent()) {
            throw new BusinessRuleException("Un lieu avec ce nom existe déjà.");
        }
        
        Lieu lieu = mapToEntity(dto);
        Lieu savedLieu = lieuRepository.save(lieu);
        return mapToDTO(savedLieu);
    }
    
    public LieuDTO updateLieu(Long id, LieuDTO dto) {
        Lieu lieu = lieuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lieu introuvable avec l'ID : " + id));
        
        lieuRepository.findByNom(dto.getNom())
                .ifPresent(l -> {
                    if (!l.getId().equals(id)) throw new BusinessRuleException("Ce nom de lieu est déjà utilisé.");
                });
                
        lieu.setNom(dto.getNom());
        lieu.setAdresse(dto.getAdresse());
        lieu.setVille(dto.getVille());
        lieu.setCapaciteMax(dto.getCapaciteMax());
        
        Lieu updatedLieu = lieuRepository.save(lieu);
        return mapToDTO(updatedLieu);
    }
    
    public void deleteLieu(Long id) {
        if (!lieuRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lieu introuvable avec l'ID : " + id);
        }
        lieuRepository.deleteById(id);
    }
    
    public LieuDTO mapToDTO(Lieu lieu) {
        LieuDTO dto = new LieuDTO();
        dto.setId(lieu.getId());
        dto.setNom(lieu.getNom());
        dto.setAdresse(lieu.getAdresse());
        dto.setVille(lieu.getVille());
        dto.setCapaciteMax(lieu.getCapaciteMax());
        return dto;
    }
    
    public Lieu mapToEntity(LieuDTO dto) {
        Lieu lieu = new Lieu();
        lieu.setNom(dto.getNom());
        lieu.setAdresse(dto.getAdresse());
        lieu.setVille(dto.getVille());
        lieu.setCapaciteMax(dto.getCapaciteMax());
        return lieu;
    }
}
