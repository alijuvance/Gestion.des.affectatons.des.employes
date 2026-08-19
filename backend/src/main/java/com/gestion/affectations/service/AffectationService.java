package com.gestion.affectations.service;

import com.gestion.affectations.domain.Affectation;
import com.gestion.affectations.domain.Employe;
import com.gestion.affectations.domain.Lieu;
import com.gestion.affectations.exception.BusinessRuleException;
import com.gestion.affectations.exception.ResourceNotFoundException;
import com.gestion.affectations.repository.AffectationRepository;
import com.gestion.affectations.repository.EmployeRepository;
import com.gestion.affectations.repository.LieuRepository;
import com.gestion.affectations.web.dto.AffectationDTO;
import com.gestion.affectations.web.dto.AffectationRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AffectationService {
    
    private final AffectationRepository affectationRepository;
    private final EmployeRepository employeRepository;
    private final LieuRepository lieuRepository;
    private final EmployeService employeService;
    private final LieuService lieuService;
    
    public List<AffectationDTO> getAllAffectations() {
        return affectationRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<AffectationDTO> getAffectationsByEmploye(Long employeId) {
        return affectationRepository.findByEmployeId(employeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public AffectationDTO createAffectation(AffectationRequestDTO request) {
        Employe employe = employeRepository.findById(request.getEmployeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable"));
        
        Lieu lieu = lieuRepository.findById(request.getLieuId())
                .orElseThrow(() -> new ResourceNotFoundException("Lieu introuvable"));
                
        // Règle métier : Clôturer l'affectation précédente si elle existe
        Optional<Affectation> affectationEnCoursOpt = affectationRepository.findByEmployeIdAndDateFinIsNull(employe.getId());
        
        if (affectationEnCoursOpt.isPresent()) {
            Affectation affectationEnCours = affectationEnCoursOpt.get();
            // On s'assure que la nouvelle affectation ne commence pas avant l'ancienne
            if (request.getDateDebut().isBefore(affectationEnCours.getDateDebut())) {
                throw new BusinessRuleException("La date de début de la nouvelle affectation ne peut pas être antérieure à la date de l'affectation en cours.");
            }
            // Clôture
            affectationEnCours.setDateFin(request.getDateDebut().minusDays(1)); // Se termine la veille
            affectationRepository.save(affectationEnCours);
        }
        
        // Créer la nouvelle affectation
        Affectation nouvelleAffectation = new Affectation();
        nouvelleAffectation.setEmploye(employe);
        nouvelleAffectation.setLieu(lieu);
        nouvelleAffectation.setDateDebut(request.getDateDebut());
        // dateFin reste null
        
        Affectation savedAffectation = affectationRepository.save(nouvelleAffectation);
        return mapToDTO(savedAffectation);
    }
    
    public void cloturerAffectation(Long id) {
        Affectation affectation = affectationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Affectation introuvable"));
                
        if (affectation.getDateFin() != null) {
            throw new BusinessRuleException("Cette affectation est déjà clôturée.");
        }
        
        affectation.setDateFin(LocalDate.now());
        affectationRepository.save(affectation);
    }
    
    private AffectationDTO mapToDTO(Affectation affectation) {
        AffectationDTO dto = new AffectationDTO();
        dto.setId(affectation.getId());
        dto.setDateDebut(affectation.getDateDebut());
        dto.setDateFin(affectation.getDateFin());
        dto.setEmploye(employeService.mapToDTO(affectation.getEmploye()));
        dto.setLieu(lieuService.mapToDTO(affectation.getLieu()));
        return dto;
    }
}
