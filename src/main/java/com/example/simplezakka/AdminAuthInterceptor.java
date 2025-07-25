package com.example.simplezakka;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("ADMIN_SESSION") != null) {
            // ログイン済みなので処理を続ける
            return true;
        }

        // ログインしていない場合は401やリダイレクトを返す
        response.sendRedirect("/admin-login.html?message=please_login");
        return false;
    }
}
