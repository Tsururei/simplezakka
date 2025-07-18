package com.example.simplezakka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MypageResponse {
    private String name;
    private String email;
    private String address;
    private String password;
}
