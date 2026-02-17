package com.domoticsweb.proy_appweb_LPII.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

    	Set<String> roles = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority().trim().toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet());

        String destino;
        
        // Orden de prioridad: ADMIN > GESTOR > USUARIO
        if (roles.contains("ROLE_ADMIN")) {
            destino = "/admin/panel";
        } else if (roles.contains("ROLE_GESTOR")) {
            destino = "/gestor";
        } else {
            destino = "/usuario/panel";
        }

        response.sendRedirect(request.getContextPath() + destino);
    }
}
