package com.domoticsweb.proy_appweb_LPII.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    private final AuthSuccessHandler authSuccessHandler;

    public SecurityConfig(AuthSuccessHandler authSuccessHandler) {
        this.authSuccessHandler = authSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
            	.requestMatchers("/api/productos/**").permitAll()
            	.requestMatchers("/api/sesion/estado").permitAll()
            	.requestMatchers("/api/pedidos/confirmar").permitAll()
                .requestMatchers("/", "/nosotros", "/contacto", "/productos", "/login", "/registro",
                        "/images/**", "/css/**", "/js/**").permitAll()

                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/usuario/**").hasAnyRole("USUARIO", "ADMIN")
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("usuario")
                .passwordParameter("contrasena")
                .successHandler(authSuccessHandler)
                .failureUrl("/login?error=true")
                .permitAll()
                .defaultSuccessUrl("/productos", true)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
            )
            .csrf(csrf -> csrf
            	    .ignoringRequestMatchers("/api/ventas/finalizar", "/api/pedidos/confirmar")
            	)
            .exceptionHandling(e -> e
                    .authenticationEntryPoint((request, response, authException) -> {
                        // Si la petición es AJAX / Fetch, devolvemos 401 en vez de redirigir
                        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With")) || 
                            request.getHeader("Accept").contains("application/json")) {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        } else {
                            response.sendRedirect("/login");
                        }
                    })
                );;

        return http.build();
    }
}
