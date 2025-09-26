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
@RequestMapping("/auth")
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
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        String role = authentication.getAuthorities().iterator().next().getAuthority(); // ROLE_USER or ROLE_ADMIN
        String token = jwtUtil.generateToken(username, role);

        return ResponseEntity.ok(token);
    }
}
