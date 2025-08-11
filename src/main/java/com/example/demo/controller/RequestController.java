package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<Request> createRequest(
            @RequestParam Long userId,
            @RequestParam Long produitId,
            @RequestParam Double quantiteDemandee) {
        // In JWT version, userId will come from the token, not a request param
        User user = new User();
        user.setId(userId);
        Produit produit = new Produit();
        produit.setId(produitId);
        return ResponseEntity.ok(requestService.createRequest(user, produit, quantiteDemandee));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Request>> getRequestsByUser(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        return ResponseEntity.ok(requestService.getRequestsByUser(user));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Request>> getPendingRequests() {
        return ResponseEntity.ok(requestService.getAllPendingRequests());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Request> approveRequest(@PathVariable Long id, @RequestParam Long adminId) {
        Admin admin = new Admin();
        admin.setId(adminId);
        return ResponseEntity.ok(requestService.approveRequest(id, admin));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Request> rejectRequest(@PathVariable Long id, @RequestParam Long adminId) {
        Admin admin = new Admin();
        admin.setId(adminId);
        return ResponseEntity.ok(requestService.rejectRequest(id, admin));
    }
}
