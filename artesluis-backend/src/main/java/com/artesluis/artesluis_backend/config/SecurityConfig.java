package com.artesluis.artesluis_backend.config;

import com.artesluis.artesluis_backend.security.CustomUserDetailsService;
import com.artesluis.artesluis_backend.security.CustomAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.http.HttpMethod;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig implements WebMvcConfigurer {

    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                         CustomAuthenticationSuccessHandler successHandler) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Configuración de autorización
            .authorizeHttpRequests(auth -> auth
                // Recursos públicos - HTML
                .requestMatchers("/", "/index", "/login", "/logout", "/demo-login").permitAll()
                .requestMatchers("/static/**", "/css/**", "/js/**", "/img/**", "/uploads/**").permitAll()
                .requestMatchers("/contacto", "/nosotros", "/mision", "/portafolio").permitAll()
                
                // Planes - Vista pública, pero acciones de carrito requieren autenticación
                .requestMatchers(HttpMethod.GET, "/planes").permitAll()
                .requestMatchers("/planes/agregar-carrito", "/planes/carrito/**").authenticated()
                
                // === APIs PÚBLICAS (sin autenticación) ===
                // Health checks y diagnósticos
                .requestMatchers("/health", "/api/health/**", "/api/test").permitAll()
                .requestMatchers("/diagnostics/**").permitAll()
                
                // Autenticación y registro
                .requestMatchers("/api/usuarios/login", "/api/usuarios/registro").permitAll()
                .requestMatchers("/api/usuarios/session-status").permitAll() // Para debugging
                
                // Estadísticas públicas
                .requestMatchers("/api/data/stats").permitAll()
                
                // Planes - GET es público (para que clientes vean los planes sin login)
                .requestMatchers(HttpMethod.GET, "/api/planes", "/api/planes/*").permitAll()
                
                // === APIs PROTEGIDAS - Requieren autenticación ===
                // Planes - Solo ADMIN puede crear/editar/eliminar
                .requestMatchers(HttpMethod.POST, "/api/planes", "/api/planes/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/planes/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/planes/*").hasRole("ADMIN")
                
                // Perfil de usuario - requiere autenticación (cualquier usuario logueado)
                .requestMatchers("/api/usuarios/perfil/**", "/api/usuarios/cambiar-password/**").authenticated()
                
                // Gestión de usuarios - ADMIN y MODERADOR
                .requestMatchers("/api/usuarios/**").hasAnyRole("ADMIN", "MODERADOR")
                
                // Roles - solo ADMIN
                .requestMatchers("/api/roles/**").hasRole("ADMIN")
                
                // Upload - ADMIN y ARTISTA
                .requestMatchers("/api/upload/**").hasAnyRole("ADMIN", "ARTISTA")
                
                // Administración - solo ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Data - POST/DELETE solo ADMIN, GET puede ser público o según endpoint
                .requestMatchers(HttpMethod.POST, "/api/data/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/data/**").hasRole("ADMIN")
                
                // Carrito y checkout - usuarios autenticados
                .requestMatchers("/carrito/**", "/checkout/**","/checkout/procesar", "/mis-ordenes").authenticated()
                
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            
            // Configuración de login
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("correo")
                .passwordParameter("password")
                .successHandler(successHandler)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            
            // Configuración de logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            
            // Configuración de sesiones
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            
            // CSRF - deshabilitado para APIs REST (considerar habilitarlo en producción)
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**", "/planes/agregar-carrito", "/planes/carrito/**")
            );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // NOTA: NoOpPasswordEncoder está deprecado y NO DEBE USARSE EN PRODUCCIÓN
        // Se usa temporalmente para mantener compatibilidad con passwords en texto plano
        // TODO: Migrar a BCryptPasswordEncoder y re-hashear todas las contraseñas
        return NoOpPasswordEncoder.getInstance();
    }

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
