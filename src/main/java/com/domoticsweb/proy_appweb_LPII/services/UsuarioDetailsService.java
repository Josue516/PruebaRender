package com.domoticsweb.proy_appweb_LPII.services;

import com.domoticsweb.proy_appweb_LPII.database.entities.Usuario;
import com.domoticsweb.proy_appweb_LPII.database.repositories.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {

        Usuario u = usuarioRepository.findByCorreoIgnoreCase(input)
                .or(() -> usuarioRepository.findByNombreUsuarioIgnoreCase(input))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + input));

        var authorities = u.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getNombre()))
                .collect(Collectors.toList());

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getNombreUsuario())   // para mostrar o identificar
                .password(u.getContrasenaHash())
                .authorities(authorities)
                .disabled(!Boolean.TRUE.equals(u.getActivo()))
                .build();
    }
}
