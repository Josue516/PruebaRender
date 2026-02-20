package com.domoticsweb.proy_appweb_LPII.database.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.domoticsweb.proy_appweb_LPII.database.entities.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    


    // Filtro por categoría desde el Sidebar 
    List<Producto> findByCategoria_IdCategoriaAndActivoTrue(Long idCategoria); 

    boolean existsByNombre(String nombre);

    @Query("SELECT p FROM Producto p WHERE " +
    	       "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
    	       "(:idCategoria IS NULL OR p.categoria.idCategoria = :idCategoria) AND " +
    	       "(:activo IS NULL OR p.activo = :activo)")
    	List<Producto> filtrarProductos(
    	    @Param("nombre") String nombre,
    	    @Param("idCategoria") Long idCategoria,
    	    @Param("activo") Boolean activo
    	);
    List<Producto> findByActivoTrueAndCategoria_ActivoTrue();
}
