package com.example.simplezakka.config;

import com.example.simplezakka.entity.Product;
import com.example.simplezakka.entity.Admin;
import com.example.simplezakka.entity.Category;
import com.example.simplezakka.entity.User;
import com.example.simplezakka.repository.ProductRepository;
import com.example.simplezakka.repository.AdminRepository;
import com.example.simplezakka.repository.UserRepository;
import com.example.simplezakka.repository.CategoryRepository;

import jakarta.persistence.GenerationType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;


    @Autowired
    public DataLoader(
        ProductRepository productRepository,
        AdminRepository adminRepository,
        UserRepository userRepository,
        CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        loadSampleProducts();
        loadSampleAdmin();
        loadSampleUser();
    }

    private void loadSampleProducts() {
        if (productRepository.count() > 0) {
            return; // すでにデータが存在する場合はスキップ
        }

        Category defaultCategory = loadOrCreateDefaultCategory();
        Category secondCategory = loadOrCreateSecondCategory();

        List<Product> products = Arrays.asList(
            createProduct(
                "シンプルデスクオーガナイザー", 
                "机の上をすっきり整理できる木製オーガナイザー。ペン、メモ、スマートフォンなどを収納できます。", 
                3500, 
                20, 
                "/images/desk-organizer.png", 
                true,
                secondCategory
            ),
            createProduct(
                "アロマディフューザー（ウッド）", 
                "天然木を使用したシンプルなデザインのアロマディフューザー。LEDライト付き。", 
                4200, 
                15, 
                "/images/aroma-diffuser.png", 
                true,
                secondCategory
            ),
            createProduct(
                "コットンブランケット", 
                "オーガニックコットン100%のやわらかブランケット。シンプルなデザインで様々なインテリアに合います。", 
                5800, 
                10, 
                "/images/cotton-blanket.png", 
                false,
                secondCategory
            ),
            createProduct(
                "ステンレスタンブラー", 
                "保温・保冷機能に優れたシンプルなデザインのステンレスタンブラー。容量350ml。", 
                2800, 
                30, 
                "/images/tumbler.png", 
                false,
                defaultCategory
            ),
            createProduct(
                "ミニマルウォールクロック", 
                "余計な装飾のないシンプルな壁掛け時計。静音設計。", 
                3200, 
                25, 
                "/images/wall-clock.png", 
                false,
                secondCategory
            ),
            createProduct(
                "リネンクッションカバー", 
                "天然リネン100%のクッションカバー。取り外して洗濯可能。45×45cm対応。", 
                2500, 
                40, 
                "/images/cushion-cover.png", 
                true,
                secondCategory
            ),
            createProduct(
                "陶器フラワーベース", 
                "手作りの風合いが魅力の陶器製フラワーベース。シンプルな形状で花を引き立てます。", 
                4000, 
                15, 
                "/images/flower-vase.png", 
                false,
                secondCategory
            ),
            createProduct(
                "木製コースター（4枚セット）", 
                "天然木を使用したシンプルなデザインのコースター。4枚セット。", 
                1800, 
                50, 
                "/images/wooden-coaster.png", 
                false,
                defaultCategory
            ),
            createProduct(
                "キャンバストートバッグ", 
                "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。", 
                3600, 
                35, 
                "/images/tote-bag.png", 
                true,
                secondCategory
            ),
            createProduct(
                "ガラス保存容器セット", 
                "電子レンジ・食洗機対応のガラス製保存容器。3サイズセット。", 
                4500, 
                20, 
                "/images/glass-container.png", 
                false,
                defaultCategory
            )
        );
        
        productRepository.saveAll(products);
    }
    
    private void loadSampleAdmin(){
        String email = "admin@example.com";
        if(adminRepository.findByAdminEmail(email).isPresent()){
            return;//すでに登録済みならスキップ
        }

        Admin admin = Admin.builder()
                .adminId(UUID.randomUUID().toString())
                .adminName("管理者 太郎")
                .adminEmail(email)
                .adminPassword(("adminpass")) // 平文のまま保存
                .adminDate(LocalDateTime.now())
                .build();

            adminRepository.save(admin);
    }

    private void loadSampleUser(){
        String email = "user@example.com";
        if(userRepository.findByUserEmail(email).isPresent()){
            return;//すでに登録済みならスキップ
        }

        User user = User.builder()
                .userName("消費者 太郎")
                .userEmail(email)
                .userPassword(("userpass")) // 平文のまま保存
                .userAddress(("東京都品川区1-2-3"))
                .userDate(LocalDateTime.now())
                .build();

            userRepository.save(user);
    }

    private Product createProduct(String name, String description, Integer price, Integer stock, String imageUrl, Boolean isRecommended, Category category) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setImageUrl(imageUrl);
        product.setIsRecommended(isRecommended);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setCategory(category);
        return product;
    }

    private Category loadOrCreateDefaultCategory() {
        String defaultCategoryId = "cate001";
        return categoryRepository.findById(defaultCategoryId).orElseGet(() -> {
            Category category = new Category();
            category.setCategoryId(defaultCategoryId);
            category.setCategoryName("キッチン用品");
            return categoryRepository.save(category);
        });
    }

    private Category loadOrCreateSecondCategory() {
        String secondCategoryId = "cate002";
        return categoryRepository.findById(secondCategoryId).orElseGet(() -> {
            Category category = new Category();
            category.setCategoryId(secondCategoryId);
            category.setCategoryName("インテリア");
            return categoryRepository.save(category);
    });
}
}
