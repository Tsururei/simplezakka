package com.example.simplezakka.dto.order;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerInfo {
    @NotBlank(message = "購入者氏名は必須です")
    private String customerName;

    @NotBlank(message = "購入者住所は必須です")
    private String customerAddress;
    
    @NotBlank(message = "購入者メールアドレスは必須です")
    @Email(message = "有効なメールアドレスを入力してください")
    private String customerEmail;
    
    @NotBlank(message = "配送先氏名は必須です")
    private String shippingName;

    @NotBlank(message = "配送先住所は必須です")
    private String shippingAddress;
    
    @NotBlank(message = "決済方法は必須です")
    private String payMethod;
}