package com.domoticsweb.proy_appweb_LPII.database.repositories;

import com.domoticsweb.proy_appweb_LPII.database.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
    Optional<Usuario> findByNombreUsuarioIgnoreCase(String nombreUsuario);
    
    boolean existsByNombreUsuarioIgnoreCaseAndIdUsuarioNot(String nombreUsuario, Integer idUsuario);

    boolean existsByCorreoIgnoreCaseAndIdUsuarioNot(String correo, Integer idUsuario);
    
    @Query("SELECT DISTINCT u FROM Usuario u " +
            "LEFT JOIN u.roles r " +
            "WHERE (:nombre IS NULL OR :nombre = '' OR " +
            "       LOWER(u.nombreUsuario) LIKE LOWER(CONCAT('%', :nombre, '%')) OR " +
            "       LOWER(u.correo) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
            "AND (:idRol IS NULL OR r.idRol = :idRol) " +
            "AND (:activo IS NULL OR u.activo = :activo)")
     List<Usuario> filtrarUsuarios(
         @Param("nombre") String nombre,
         @Param("idRol") Long idRol,
         @Param("activo") Boolean activo
     );
}
