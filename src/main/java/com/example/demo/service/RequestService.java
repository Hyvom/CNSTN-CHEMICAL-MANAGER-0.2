package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.ProduitRepository;
import com.example.demo.repository.RequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final ProduitRepository produitRepository;

    public RequestService(RequestRepository requestRepository, ProduitRepository produitRepository) {
        this.requestRepository = requestRepository;
        this.produitRepository = produitRepository;
    }

    public Request createRequest(User user, Produit produit, Double quantiteDemandee) {
        Request request = new Request();
        request.setUser(user);
        request.setProduit(produit);
        request.setQuantiteDemandee(quantiteDemandee);
        request.setStatus(RequestStatus.PENDING);
        request.setDateRequest(LocalDateTime.now());
        return requestRepository.save(request);
    }

    public List<Request> getRequestsByUser(User user) {
        return requestRepository.findByUser(user);
    }

    public List<Request> getAllPendingRequests() {
        return requestRepository.findByStatus(RequestStatus.PENDING);
    }

    @Transactional
    public Request approveRequest(Long requestId, Admin admin) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        Produit produit = produitRepository.findById(request.getProduit().getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if product is expired
        if (produit.getDateExpiration() != null && produit.getDateExpiration().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot approve request: product is expired");
        }

        // Check if enough quantity is available
        if (request.getQuantiteDemandee() > produit.getQuantite()) {
            throw new RuntimeException("Cannot approve request: not enough stock");
        }

        // Decrease stock
        produit.setQuantite(produit.getQuantite() - request.getQuantiteDemandee());
        produitRepository.save(produit);

        // Approve request
        request.setStatus(RequestStatus.APPROVED);
        request.setValidatedBy(admin);
        request.setDateValidation(LocalDateTime.now());

        return requestRepository.save(request);
    }

    public Request rejectRequest(Long requestId, Admin admin) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(RequestStatus.REJECTED);
        request.setValidatedBy(admin);
        request.setDateValidation(LocalDateTime.now());
        return requestRepository.save(request);
    }
}
