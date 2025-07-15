package com.example.simplezakka.controller;

import com.example.simplezakka.entity.Admin;
import com.example.simplezakka.repository.AdminRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admins")
@CrossOrigin(origins = "*") // JSからのアクセスを許可
public class AdminEditController {

    private final AdminRepository repo;

    public AdminEditController(AdminRepository repo) {
        this.repo = repo;
    }

    // 管理者一覧取得
    @GetMapping
    public List<Admin> getAllAdmins() {
        return repo.findAll();
    }

    // 管理者新規登録
    @PostMapping
    public Admin createAdmin(@RequestBody Admin admin) {
        admin.setAdminDate(LocalDateTime.now());   // 登録日時を現在時刻にセット
        admin.setAdminPassword("******");          // ここはハッシュ化推奨
        return repo.save(admin);
    }

    // 管理者削除
    @DeleteMapping("/{id}")
    public void deleteAdmin(@PathVariable String id) {  // IDの型はString
        repo.deleteById(id);
    }
}
