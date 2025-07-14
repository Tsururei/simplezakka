package com.example.simplezakka.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;
    
    @Column(nullable = false, name = "user_name")
    private String userName;

    @Column(nullable = false, name = "user_email")
    private String userEmail;

    @Column(nullable = false, name = "user_password")
    private String userPassword;
    
    @Column(nullable = false, name = "user_address")
    private String userAddress;
    
    @Column(nullable = false, updatable = false, name = "user_date")
    private LocalDateTime userDate;

}
