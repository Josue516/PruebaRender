package com.domoticsweb.proy_appweb_LPII.database.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.domoticsweb.proy_appweb_LPII.database.entities.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
	List<Producto> findByActivoTrue();  //Productos disponibles

    List<Producto> findByCategoria_IdCategoria(Long idCategoria); //Lista productos por categoria

    List<Producto> findByNombreContainingIgnoreCase(String nombre); //Buscador para tienda
    
    boolean existsByNombre(String nombre);
}
