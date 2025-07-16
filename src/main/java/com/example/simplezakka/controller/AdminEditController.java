package com.example.simplezakka.controller;

import com.example.simplezakka.entity.Admin;
import com.example.simplezakka.repository.AdminRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/admins")
@CrossOrigin(origins = "*") 
public class AdminEditController {

    private final AdminRepository repo;

    public AdminEditController(AdminRepository repo) {
        this.repo = repo;
    }


    @GetMapping
    public List<Admin> getAllAdmins() {
        return repo.findAll();
    }


    @PostMapping
    public Admin createAdmin(@RequestBody Admin admin) {
    admin.setAdminId(UUID.randomUUID().toString());     
    admin.setAdminDate(LocalDateTime.now());            
    return repo.save(admin);                           
    }

    
    @DeleteMapping("/{id}")
    public void deleteAdmin(@PathVariable String id) {  
        repo.deleteById(id);
    }
}
