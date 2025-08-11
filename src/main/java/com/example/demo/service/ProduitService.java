package com.example.demo.service;

import com.example.demo.entity.Produit;
import com.example.demo.repository.ProduitRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProduitService {

    private final ProduitRepository produitRepository;

    public ProduitService(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    public Produit addProduit(Produit produit) {
        return produitRepository.save(produit);
    }

    public List<Produit> getAllProduits() {
        return produitRepository.findAll();
    }

    public Optional<Produit> getProduitById(Long id) {
        return produitRepository.findById(id);
    }

    public Produit updateProduit(Long id, Produit updatedProduit) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit not found"));
        produit.setNom(updatedProduit.getNom());
        produit.setDescription(updatedProduit.getDescription());
        produit.setQuantite(updatedProduit.getQuantite());
        produit.setUnite(updatedProduit.getUnite());
        produit.setDateExpiration(updatedProduit.getDateExpiration());
        produit.setFournisseur(updatedProduit.getFournisseur());
        produit.setCategorie(updatedProduit.getCategorie());
        return produitRepository.save(produit);
    }

    public void deleteProduit(Long id) {
        produitRepository.deleteById(id);
    }
}
