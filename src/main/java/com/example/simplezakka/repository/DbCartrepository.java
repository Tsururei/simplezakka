package com.example.simplezakka.repository;
import com.example.simplezakka.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DbCartrepository extends JpaRepository<Cart, Integer>{
    public Optional<Cart> findByUserId(Integer userId);
    public void deleteByUserId(Integer userId);
  
}   