package com.domoticsweb.proy_appweb_LPII.services;

import com.domoticsweb.proy_appweb_LPII.database.entities.Rol;
import com.domoticsweb.proy_appweb_LPII.database.entities.Usuario;
import com.domoticsweb.proy_appweb_LPII.database.repositories.RolRepository;
import com.domoticsweb.proy_appweb_LPII.database.repositories.UsuarioRepository;
import com.domoticsweb.proy_appweb_LPII.dto.RegistroRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistroService {

    private final UsuarioRepository usuarioRepo;
    private final RolRepository rolRepo;
    private final PasswordEncoder passwordEncoder;

    public void registrarUsuario(RegistroRequest req) {

        if (usuarioRepo.findByNombreUsuarioIgnoreCase(req.getNombreUsuario()).isPresent())
            throw new IllegalArgumentException("Ese nombre de usuario ya existe.");

        if (usuarioRepo.findByCorreoIgnoreCase(req.getCorreo()).isPresent())
            throw new IllegalArgumentException("Ese correo ya está registrado.");

        Rol rolUsuario = rolRepo.findByNombre("USUARIO")
                .orElseThrow(() -> new IllegalStateException("No existe el rol USUARIO en la BD."));

        Usuario u = new Usuario();
        u.setNombreUsuario(req.getNombreUsuario());
        u.setCorreo(req.getCorreo());
        u.setContrasenaHash(passwordEncoder.encode(req.getContrasena()));
        u.setActivo(true);
        u.getRoles().add(rolUsuario);

        usuarioRepo.save(u);
    }
}
