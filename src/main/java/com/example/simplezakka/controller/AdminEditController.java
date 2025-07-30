package com.example.simplezakka.controller;

import com.example.simplezakka.entity.Admin;
import com.example.simplezakka.repository.AdminRepository;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/admins")
@CrossOrigin(origins = "*") 
public class AdminEditController {

    private final AdminRepository repo;
    private int count;

    public AdminEditController(AdminRepository repo) {
        this.repo = repo;
    }


    @GetMapping
    public List<Admin> getAllAdmins() {
        return repo.findAll();
    }


    @PostMapping
    public ResponseEntity<?> createAdmin(@Valid @RequestBody Admin admin, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
        String errorMessage = bindingResult.getFieldError().getDefaultMessage();
        return ResponseEntity.badRequest().body(errorMessage);
    }
        if (repo.findByAdminEmail(admin.getAdminEmail()).isPresent()) {
        return ResponseEntity.badRequest().body("メールアドレスは既に登録されています");
    }
    
    admin.setAdminId(UUID.randomUUID().toString());     
    admin.setAdminDate(LocalDateTime.now());   
    
    Admin savedAdmin = repo.save(admin);
    return ResponseEntity.ok(savedAdmin);                          
    }

    
    @DeleteMapping("/{id}")
public ResponseEntity<?> deleteAdmin(@PathVariable String id) {
    long adminCount = repo.count();

    if (adminCount <= 1) {
        return ResponseEntity.badRequest().body("少なくとも1人の管理者は必要です。削除できません。");
    }

    repo.deleteById(id);
    return ResponseEntity.ok("管理者を削除しました");
}
}
