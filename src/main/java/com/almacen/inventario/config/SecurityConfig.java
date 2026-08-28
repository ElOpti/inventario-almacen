package com.almacen.inventario.config;

import com.almacen.inventario.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_ADMINISTRADOR") || a.getAuthority().equalsIgnoreCase("ADMINISTRADOR"));
            boolean isAlmacenista = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_ALMACENISTA") || a.getAuthority().equalsIgnoreCase("ALMACENISTA"));

            if (isAdmin) {
                response.sendRedirect("/inventario");
            } else if (isAlmacenista) {
                response.sendRedirect("/salidas");
            } else {
                response.sendRedirect("/inventario");
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos y páginas públicas
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico", "/h2-console/**").permitAll()
                .requestMatchers("/login").permitAll()
                
                // Módulo de Salida de Productos: Exclusivo para ALMACENISTA
                .requestMatchers("/salidas", "/salidas/**").hasAnyAuthority("ROLE_ALMACENISTA", "ALMACENISTA")
                
                // Módulo de Histórico de Movimientos: Exclusivo para ADMINISTRADOR
                .requestMatchers("/historial", "/historial/**").hasAnyAuthority("ROLE_ADMINISTRADOR", "ADMINISTRADOR")

                // Módulo de Gestión de Usuarios (CRUD): Exclusivo para ADMINISTRADOR
                .requestMatchers("/usuarios", "/usuarios/**").hasAnyAuthority("ROLE_ADMINISTRADOR", "ADMINISTRADOR")
                
                // Operaciones de gestión en Inventario: Exclusivo para ADMINISTRADOR
                .requestMatchers(
                    "/inventario/nuevo", 
                    "/inventario/guardar", 
                    "/inventario/entrada", 
                    "/inventario/estado/**", 
                    "/inventario/editar/**"
                ).hasAnyAuthority("ROLE_ADMINISTRADOR", "ADMINISTRADOR")
                
                // Visualización de Inventario: Permitido para ADMINISTRADOR y ALMACENISTA
                .requestMatchers("/inventario", "/inventario/").hasAnyAuthority("ROLE_ADMINISTRADOR", "ADMINISTRADOR", "ROLE_ALMACENISTA", "ALMACENISTA")
                
                // Redirección raíz y dashboard
                .requestMatchers("/", "/dashboard").authenticated()
                
                // Cualquier otra solicitud requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(customAuthenticationSuccessHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403")
            )
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }
}
