package com.example.demo.controller;

import com.example.demo.entity.Admin;
import com.example.demo.entity.User;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepo;
    private final AdminRepository adminRepo;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder, UserRepository userRepo,
                          AdminRepository adminRepo) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
        this.adminRepo = adminRepo;
    }

    // ---------------- USER REGISTRATION ----------------
    @PostMapping("/register/user")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepo.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if (userRepo.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    // ---------------- ADMIN REGISTRATION ----------------
    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {
        if (adminRepo.existsByUsername(admin.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if (adminRepo.existsByEmail(admin.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminRepo.save(admin);
        return ResponseEntity.ok("Admin registered successfully");
    }

    // ---------------- LOGIN (USER OR ADMIN) ----------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        String role = authentication.getAuthorities().iterator().next().getAuthority(); // ROLE_USER or ROLE_ADMIN
        String token = jwtUtil.generateToken(username, role);

        return ResponseEntity.ok(token);
    }
}
