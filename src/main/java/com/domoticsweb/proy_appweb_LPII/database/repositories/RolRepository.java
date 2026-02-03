package com.domoticsweb.proy_appweb_LPII.database.repositories;

import com.domoticsweb.proy_appweb_LPII.database.entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
}
