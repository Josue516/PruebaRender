package com.domoticsweb.proy_appweb_LPII.database.repositories;

import com.domoticsweb.proy_appweb_LPII.database.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
    Optional<Usuario> findByNombreUsuarioIgnoreCase(String nombreUsuario);
    
    boolean existsByNombreUsuarioIgnoreCaseAndIdUsuarioNot(String nombreUsuario, Integer idUsuario);

    boolean existsByCorreoIgnoreCaseAndIdUsuarioNot(String correo, Integer idUsuario);

}
