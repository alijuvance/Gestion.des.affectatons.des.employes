package com.gestion.affectations.service;

import com.gestion.affectations.domain.Employe;
import com.gestion.affectations.exception.BusinessRuleException;
import com.gestion.affectations.exception.ResourceNotFoundException;
import com.gestion.affectations.repository.EmployeRepository;
import com.gestion.affectations.web.dto.EmployeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeService {
    private final EmployeRepository employeRepository;
    private final com.gestion.affectations.repository.AffectationRepository affectationRepository;
    
    public List<EmployeDTO> getAllEmployes() {
        return employeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public EmployeDTO getEmployeById(Long id) {
        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable avec l'ID : " + id));
        return mapToDTO(employe);
    }
    
    public EmployeDTO createEmploye(EmployeDTO dto) {
        if (employeRepository.findByMatricule(dto.getMatricule()).isPresent()) {
            throw new BusinessRuleException("Un employé avec ce matricule existe déjà.");
        }
        if (employeRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessRuleException("Un employé avec cet email existe déjà.");
        }
        
        Employe employe = mapToEntity(dto);
        Employe savedEmploye = employeRepository.save(employe);
        return mapToDTO(savedEmploye);
    }
    
    public EmployeDTO updateEmploye(Long id, EmployeDTO dto) {
        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable avec l'ID : " + id));
        
        // Vérifier si le nouveau matricule n'est pas déjà pris par un AUTRE employé
        employeRepository.findByMatricule(dto.getMatricule())
                .ifPresent(e -> {
                    if (!e.getId().equals(id)) throw new BusinessRuleException("Ce matricule est déjà utilisé.");
                });
                
        // Même chose pour l'email
        employeRepository.findByEmail(dto.getEmail())
                .ifPresent(e -> {
                    if (!e.getId().equals(id)) throw new BusinessRuleException("Cet email est déjà utilisé.");
                });
                
        employe.setMatricule(dto.getMatricule());
        employe.setNom(dto.getNom());
        employe.setPrenom(dto.getPrenom());
        employe.setEmail(dto.getEmail());
        employe.setTelephone(dto.getTelephone());
        employe.setFonction(dto.getFonction());
        employe.setDateEmbauche(dto.getDateEmbauche());
        
        Employe updatedEmploye = employeRepository.save(employe);
        return mapToDTO(updatedEmploye);
    }
    
    public void deleteEmploye(Long id) {
        if (!employeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employé introuvable avec l'ID : " + id);
        }
        employeRepository.deleteById(id);
    }
    
    // Utilitaires de mapping manuel
    public EmployeDTO mapToDTO(Employe employe) {
        EmployeDTO dto = new EmployeDTO();
        dto.setId(employe.getId());
        dto.setMatricule(employe.getMatricule());
        dto.setNom(employe.getNom());
        dto.setPrenom(employe.getPrenom());
        dto.setEmail(employe.getEmail());
        dto.setTelephone(employe.getTelephone());
        dto.setFonction(employe.getFonction());
        dto.setDateEmbauche(employe.getDateEmbauche());
        
        affectationRepository.findActiveByEmployeId(employe.getId())
                .ifPresent(affectation -> dto.setLieuActuel(affectation.getLieu().getNom()));
                
        return dto;
    }
    
    public Employe mapToEntity(EmployeDTO dto) {
        Employe employe = new Employe();
        employe.setMatricule(dto.getMatricule());
        employe.setNom(dto.getNom());
        employe.setPrenom(dto.getPrenom());
        employe.setEmail(dto.getEmail());
        employe.setTelephone(dto.getTelephone());
        employe.setFonction(dto.getFonction());
        employe.setDateEmbauche(dto.getDateEmbauche());
        return employe;
    }
}
