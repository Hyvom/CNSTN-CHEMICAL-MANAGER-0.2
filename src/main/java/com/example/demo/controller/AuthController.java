package com.example.demo.controller;

import com.example.demo.entity.Admin;
import com.example.demo.entity.User;
import com.example.demo.service.AdminService;
import com.example.demo.service.UserService;
import com.example.demo.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final AdminService adminService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                          UserService userService, AdminService adminService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.adminService = adminService;
    }

    // ---------------- USER REGISTRATION ----------------
    @PostMapping("/register/user")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User createdUser = userService.registerUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ---------------- ADMIN REGISTRATION ----------------
    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {
        try {
            Admin createdAdmin = adminService.registerAdmin(admin);
            return ResponseEntity.ok(createdAdmin);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ---------------- LOGIN (USER OR ADMIN) ----------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            String role = authentication.getAuthorities().iterator().next().getAuthority(); // ROLE_USER or ROLE_ADMIN
            String token = jwtUtil.generateToken(loginRequest.username(), role);

            return ResponseEntity.ok(new LoginResponse(token, role));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    // ----------------- DTO Classes -----------------
    public record LoginRequest(String username, String password) {}

    public record LoginResponse(String token, String role) {}
}
