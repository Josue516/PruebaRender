package com.domoticsweb.proy_appweb_LPII.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        boolean esAdmin = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority().trim().toUpperCase(Locale.ROOT))
                .anyMatch("ROLE_ADMIN"::equals);

        String destino = esAdmin ? "/admin/panel" : "/usuario/panel";
        response.sendRedirect(request.getContextPath() + destino);
    }
}
