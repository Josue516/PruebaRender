package com.domoticsweb.proy_appweb_LPII.database.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.domoticsweb.proy_appweb_LPII.database.entities.Inventario;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
	Optional<Inventario> findByProducto_IdProducto(Long idProducto); //Obtener inventario por producto

    List<Inventario> findByStockLessThanEqual(Integer stock); //Detecta productos con poco stock
}
