package com.example.demo.service;

import com.example.demo.entity.Admin;
import com.example.demo.repository.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Admin registerAdmin(Admin admin) {
        if (adminRepository.existsByUsername(admin.getUsername())) {
            throw new RuntimeException("Admin username already taken");
        }
        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new RuntimeException("Admin email already registered");
        }
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }

    public void deleteAdmin(Long id) {
        adminRepository.deleteById(id);
    }
    public Admin updateAdmin(Long id, Admin updatedAdmin) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        admin.setUsername(updatedAdmin.getUsername());
        admin.setEmail(updatedAdmin.getEmail());

        // Only update password if provided
        if (updatedAdmin.getPassword() != null && !updatedAdmin.getPassword().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(updatedAdmin.getPassword()));
        }

        admin.setEnabled(updatedAdmin.isEnabled());

        return adminRepository.save(admin);
    }

}
