package com.example.simplezakka.repository;

import com.example.simplezakka.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public Optional<User> findByUserEmail(String email);
}