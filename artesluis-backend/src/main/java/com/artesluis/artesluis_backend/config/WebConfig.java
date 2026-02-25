package com.artesluis.artesluis_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private UserSessionInterceptor userSessionInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configuración para archivos estáticos con cache control mejorado
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600)
                .resourceChain(true);
        
        // Configuración específica para imágenes
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/")
                .setCachePeriod(3600)
                .resourceChain(true);
        
        // Configuración para CSS
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/")
                .setCachePeriod(1800) // Cache más corto para CSS
                .resourceChain(true);
        
        // Configuración para JS con cache más corto para evitar problemas de cache
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCachePeriod(900) // Cache más corto para JS
                .resourceChain(true);
        
        // Configuración para uploads (archivos subidos)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/")
                .setCachePeriod(0); // No cache para archivos subidos
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Registrar interceptor para agregar información de usuario y carrito a todas las vistas
        registry.addInterceptor(userSessionInterceptor)
                .addPathPatterns("/**") // Aplicar a todas las rutas
                .excludePathPatterns("/api/**", "/static/**", "/css/**", "/js/**", "/img/**", "/uploads/**"); // Excluir APIs y recursos estáticos
    }
}