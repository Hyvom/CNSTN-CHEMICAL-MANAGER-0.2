package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "http://localhost:4200")
public class RequestController {

    // ✅ All dependencies
    private final RequestService requestService;
    private final UserRepository userRepository;
    private final ProduitRepository produitRepository;
    private final RequestRepository requestRepository;

    // ✅ Single constructor with all dependencies
    public RequestController(
        RequestService requestService,
        UserRepository userRepository,
        ProduitRepository produitRepository,
        RequestRepository requestRepository
    ) {
        this.requestService = requestService;
        this.userRepository = userRepository;
        this.produitRepository = produitRepository;
        this.requestRepository = requestRepository;
    }

    // DTO for receiving request data from frontend
    public static class RequestDTO {
        private Long produitId;
        private Integer quantiteDemandee;
        private String dateDebut;
        private String dateFin;
        private String motif;

        // Getters and setters
        public Long getProduitId() { return produitId; }
        public void setProduitId(Long produitId) { this.produitId = produitId; }
        public Integer getQuantiteDemandee() { return quantiteDemandee; }
        public void setQuantiteDemandee(Integer quantiteDemandee) { this.quantiteDemandee = quantiteDemandee; }
        public String getDateDebut() { return dateDebut; }
        public void setDateDebut(String dateDebut) { this.dateDebut = dateDebut; }
        public String getDateFin() { return dateFin; }
        public void setDateFin(String dateFin) { this.dateFin = dateFin; }
        public String getMotif() { return motif; }
        public void setMotif(String motif) { this.motif = motif; }
    }

    // ✅ CREATE REQUEST - Fixed to extract user from JWT
    @PostMapping
    public ResponseEntity<Request> createRequest(
        @RequestBody RequestDTO requestDTO,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        // Get the authenticated username from JWT
        String username = userDetails.getUsername();
        
        // Load the actual User entity from database
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Load the actual Product entity from database
        Produit produit = produitRepository.findById(requestDTO.getProduitId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Create request with user, product, and quantity
        Request request = requestService.createRequest(
            user, 
            produit, 
            requestDTO.getQuantiteDemandee().doubleValue()
        );
        
        // Set additional fields
        request.setDateDebut(requestDTO.getDateDebut());
        request.setDateFin(requestDTO.getDateFin());
        request.setMotif(requestDTO.getMotif());
        
        // Save again with all fields
        request = requestRepository.save(request);
        
        return ResponseEntity.ok(request);
    }

    // ✅ GET ALL REQUESTS
    @GetMapping
    public ResponseEntity<List<Request>> getAllRequests() {
        return ResponseEntity.ok(requestService.getAllPendingRequests());
    }

    // ✅ GET REQUEST BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Request> getRequestById(@PathVariable Long id) {
        Request request = requestRepository.findById(id)
            .orElse(null);
        if (request == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(request);
    }

    // ✅ GET REQUESTS BY USER ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Request>> getRequestsByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(requestService.getRequestsByUser(user));
    }

    // ✅ GET PENDING REQUESTS
    @GetMapping("/pending")
    public ResponseEntity<List<Request>> getPendingRequests() {
        return ResponseEntity.ok(requestService.getAllPendingRequests());
    }

    // ✅ UPDATE REQUEST
    @PutMapping("/{id}")
    public ResponseEntity<Request> updateRequest(
        @PathVariable Long id, 
        @RequestBody RequestDTO requestDTO,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Request request = requestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Request not found"));
        
        // Update fields
        if (requestDTO.getQuantiteDemandee() != null) {
            request.setQuantiteDemandee(requestDTO.getQuantiteDemandee().doubleValue());
        }
        if (requestDTO.getDateDebut() != null) {
            request.setDateDebut(requestDTO.getDateDebut());
        }
        if (requestDTO.getDateFin() != null) {
            request.setDateFin(requestDTO.getDateFin());
        }
        if (requestDTO.getMotif() != null) {
            request.setMotif(requestDTO.getMotif());
        }
        
        request = requestRepository.save(request);
        return ResponseEntity.ok(request);
    }

    // ✅ APPROVE REQUEST
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(
        @PathVariable Long id, 
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            // Get admin from JWT
            String username = userDetails.getUsername();
            // Assuming you have an AdminRepository with findByUsername method
            // For now, using a placeholder admin ID
            Admin admin = new Admin(); 
            admin.setId(1L); // You should load this from database using username
            
            Request approvedRequest = requestService.approveRequest(id, admin);
            return ResponseEntity.ok(approvedRequest);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ✅ REJECT REQUEST
    @PutMapping("/{id}/reject")
    public ResponseEntity<Request> rejectRequest(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        // Get admin from JWT
        String username = userDetails.getUsername();
        Admin admin = new Admin(); 
        admin.setId(1L); // You should load this from database using username
        
        return ResponseEntity.ok(requestService.rejectRequest(id, admin));
    }

    // ✅ DELETE REQUEST
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRequest(@PathVariable Long id) {
        requestRepository.deleteById(id);
        return ResponseEntity.ok("Request deleted successfully");
    }
}
