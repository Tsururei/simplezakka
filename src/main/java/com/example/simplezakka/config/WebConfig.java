package com.example.simplezakka.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.simplezakka.AdminAuthInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminAuthInterceptor adminAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/admin-top.html",
                                             "/admin-edit.html",
                                             "/admin-orderlist.html",
                                             "/admin-product.html")  // 管理者用URLに限定して認証チェック
                .excludePathPatterns("/admin-login.html"); // ログイン関連は除外
    }
}

