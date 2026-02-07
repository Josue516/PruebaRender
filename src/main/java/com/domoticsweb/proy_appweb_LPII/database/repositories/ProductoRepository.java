package com.domoticsweb.proy_appweb_LPII.database.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.domoticsweb.proy_appweb_LPII.database.entities.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Carga inicial de la tienda si esta activo
    List<Producto> findByActivoTrue();  

    // Filtro por categoría desde el Sidebar 
    List<Producto> findByCategoria_IdCategoriaAndActivoTrue(Long idCategoria); 

    boolean existsByNombre(String nombre);
    // PANEL ADMINISTRADOR 
    // Para el Panel de Administración (Opcional si quieres ver inactivos)
    List<Producto> findByCategoria_IdCategoria(Long idCategoria);
    // El administrador necesita buscar por nombre incluso productos inactivos
    List<Producto> findByNombreContainingIgnoreCase(String nombre); 
    
}
