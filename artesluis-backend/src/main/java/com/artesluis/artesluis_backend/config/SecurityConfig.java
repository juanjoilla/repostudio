package com.artesluis.artesluis_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HeadersInterceptor());
    }
    
    public static class HeadersInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            // Configurar CSP para permitir recursos de Bootstrap CDN
            response.setHeader("Content-Security-Policy", 
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline' 'unsafe-eval' " +
                "https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
                "style-src 'self' 'unsafe-inline' " +
                "https://cdn.jsdelivr.net https://fonts.googleapis.com https://cdnjs.cloudflare.com; " +
                "font-src 'self' https://fonts.gstatic.com https://cdnjs.cloudflare.com https://cdn.jsdelivr.net; " +
                "img-src 'self' data: https:; " +
                "connect-src 'self' https://cdn.jsdelivr.net;"
            );
            return true;
        }
    }
}