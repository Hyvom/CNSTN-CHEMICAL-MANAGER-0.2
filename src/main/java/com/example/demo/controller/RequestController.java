package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "http://localhost:4200")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
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

    @PostMapping
    public ResponseEntity<Request> createRequest(@RequestBody RequestDTO requestDTO) {
        // TODO: Get userId from JWT token instead of hardcoding
        Long userId = 1L;
        User user = new User(); user.setId(userId);
        Produit produit = new Produit(); produit.setId(requestDTO.getProduitId());
        Request request = requestService.createRequest(user, produit, requestDTO.getQuantiteDemandee().doubleValue());
        // Optionally set dateDebut, dateFin, motif
        return ResponseEntity.ok(request);
    }

    @GetMapping
    public ResponseEntity<List<Request>> getAllRequests() {
        return ResponseEntity.ok(requestService.getAllPendingRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Request> getRequestById(@PathVariable Long id) {
        Request request = requestService.getRequestsByUser(new User()).stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (request == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(request);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Request>> getRequestsByUser(@PathVariable Long userId) {
        User user = new User(); user.setId(userId);
        return ResponseEntity.ok(requestService.getRequestsByUser(user));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Request>> getPendingRequests() {
        return ResponseEntity.ok(requestService.getAllPendingRequests());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Request> updateRequest(@PathVariable Long id, @RequestBody RequestDTO requestDTO) {
        return ResponseEntity.ok(new Request());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable Long id, @RequestParam(required = false) Long adminId) {
        if (adminId == null) { adminId = 1L; }
        Admin admin = new Admin(); admin.setId(adminId);
        try {
            Request approvedRequest = requestService.approveRequest(id, admin);
            return ResponseEntity.ok(approvedRequest);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Request> rejectRequest(@PathVariable Long id, @RequestParam(required = false) Long adminId) {
        if (adminId == null) { adminId = 1L; }
        Admin admin = new Admin(); admin.setId(adminId);
        return ResponseEntity.ok(requestService.rejectRequest(id, admin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRequest(@PathVariable Long id) {
        return ResponseEntity.ok("Request deleted");
    }
}
