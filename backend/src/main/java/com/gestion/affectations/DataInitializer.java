package com.gestion.affectations;

import com.gestion.affectations.domain.Utilisateur;
import com.gestion.affectations.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (utilisateurRepository.findByUsername("admin").isEmpty()) {
            Utilisateur admin = new Utilisateur();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin"));
            utilisateurRepository.save(admin);
            System.out.println("Utilisateur admin créé avec succès (username: admin, password: admin)");
        }
    }
}
