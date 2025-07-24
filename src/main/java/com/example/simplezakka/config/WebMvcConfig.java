package com.example.simplezakka.config;

import com.example.simplezakka.interceptor.AuthInterceptor;
import com.example.simplezakka.service.JwtTokenProvider;
import com.example.simplezakka.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(jwtTokenProvider, refreshTokenService))
                .addPathPatterns("/user/**", "/usercart/**", "/user/orders/**");
    }
}
