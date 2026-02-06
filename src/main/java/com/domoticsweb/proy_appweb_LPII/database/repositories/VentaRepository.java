package com.domoticsweb.proy_appweb_LPII.database.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.domoticsweb.proy_appweb_LPII.database.entities.Usuario;
import com.domoticsweb.proy_appweb_LPII.database.entities.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {
	// Para mostrar el historial al usuario logueado (Ordenado por fecha)
    List<Venta> findByUsuarioOrderByFechaVentaDesc(Usuario usuario);

    // Para buscar ventas por un rango de fechas (Reportes)
    List<Venta> findByFechaVentaBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // TODOS ESTOS METODOS, ESPECIALMENTE LOS DE ABAJO SON PARA SU FUTURO USO EN LOS DASHBOARD DE ADMIN/GESTOR
    // Para buscar ventas por estado (PAGADO, PENDIENTE, CANCELADO)
    List<Venta> findByEstado(String estado);

    // Para ver cuánto dinero se ha ganado (Suma total)
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.estado = 'PAGADO'")
    Double sumTotalVentasPagadas();

    // Para buscar por nombre de usuario directamente (Barra de búsqueda del admin)
    List<Venta> findByUsuario_NombreUsuarioContainingIgnoreCase(String nombre);
    
    
}