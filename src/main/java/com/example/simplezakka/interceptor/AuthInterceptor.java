package com.example.simplezakka.interceptor;

import com.example.simplezakka.service.JwtTokenProvider;
import com.example.simplezakka.service.RefreshTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthInterceptor(JwtTokenProvider tokenProvider, RefreshTokenService refreshTokenService) {
        this.tokenProvider = tokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String accessToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("X-Refresh-Token");
        try {
            tokenProvider.getUserIdFromToken(accessToken);
            return true;
        } catch (ExpiredJwtException e) {
            if (tokenProvider.isTokenValid(refreshToken)) {
                String newAccessToken = refreshTokenService.reissueAccessToken(refreshToken);
                response.setHeader("X-New-Access-Token", newAccessToken); 
                return true;
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}
