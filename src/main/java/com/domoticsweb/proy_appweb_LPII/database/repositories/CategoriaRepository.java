package com.domoticsweb.proy_appweb_LPII.database.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.domoticsweb.proy_appweb_LPII.database.entities.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
	Optional<Categoria> findByNombreIgnoreCase(String nombre);  //Busca por nombre ignorando mayusculas

    List<Categoria> findByActivoTrue(); //Lista solo categorias activas
    
    Optional<Categoria> findByNombre(String nombre);
}