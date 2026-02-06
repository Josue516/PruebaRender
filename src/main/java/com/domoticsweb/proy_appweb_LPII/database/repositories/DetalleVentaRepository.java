package com.domoticsweb.proy_appweb_LPII.database.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.domoticsweb.proy_appweb_LPII.database.entities.DetalleVenta;
import com.domoticsweb.proy_appweb_LPII.database.entities.Producto;
import com.domoticsweb.proy_appweb_LPII.database.entities.Venta;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
	// Obtener todos los productos de una venta específica
    List<DetalleVenta> findByVenta(Venta venta);

    // Opcional: Obtener detalles usando solo el ID de la venta, lo dejo porasiaca
    List<DetalleVenta> findByVenta_IdVenta(Long idVenta);

    // Para estadísticas: ¿Cuánto se ha vendido de un producto?
    List<DetalleVenta> findByProducto(Producto producto);
}