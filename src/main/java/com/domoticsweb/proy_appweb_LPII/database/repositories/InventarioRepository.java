package com.domoticsweb.proy_appweb_LPII.database.repositories;

import java.util.List;
import java.util.Optional;

import com.domoticsweb.proy_appweb_LPII.database.entities.EstadoVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.domoticsweb.proy_appweb_LPII.database.entities.Inventario;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
	
    @Query("SELECT i FROM Inventario i WHERE i.producto.idProducto = :idProducto")
    Optional<Inventario> findByProductoId(@Param("idProducto") Long idProducto);

    @Query("SELECT i FROM Inventario i " +
            "JOIN i.producto p " +
            "WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
            "AND (:idCategoria IS NULL OR p.categoria.idCategoria = :idCategoria) " +
            "AND (:estado IS NULL OR " +
            "     (:estado = 'bajo' AND i.stock <= i.stockMinimo) OR " +
            "     (:estado = 'normal' AND i.stock > i.stockMinimo))")
     List<Inventario> filtrarInventario(
         @Param("nombre") String nombre,
         @Param("idCategoria") Long idCategoria,
         @Param("estado") EstadoVenta estado
     );
}
