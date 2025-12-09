package com.example.gRPC.services;


import com.example.gRPC.entities.Compte;
import com.example.gRPC.repositories.CompteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompteService {

    private final CompteRepository compteRepository;

    @Autowired
    public CompteService(CompteRepository compteRepository) {
        this.compteRepository = compteRepository;
    }

    /**
     * Récupérer tous les comptes
     */
    public List<Compte> findAllComptes() {
        return compteRepository.findAll();
    }

    /**
     * Récupérer un compte par son ID
     */
    public Compte findCompteById(String id) {
        return compteRepository.findById(id).orElse(null);
    }

    /**
     * Sauvegarder un compte
     */
    public Compte saveCompte(Compte compte) {
        return compteRepository.save(compte);
    }

    /**
     * Supprimer un compte
     */
    public void deleteCompte(String id) {
        compteRepository.deleteById(id);
    }

    /**
     * Calculer la somme des soldes
     */
    public float calculateTotalSolde() {
        return (float) compteRepository.findAll()
                .stream()
                .mapToDouble(Compte::getSolde)
                .sum();
    }

    /**
     * Calculer la moyenne des soldes
     */
    public float calculateAverageSolde() {
        List<Compte> comptes = compteRepository.findAll();
        if (comptes.isEmpty()) {
            return 0;
        }
        return (float) comptes.stream()
                .mapToDouble(Compte::getSolde)
                .average()
                .orElse(0);
    }
}