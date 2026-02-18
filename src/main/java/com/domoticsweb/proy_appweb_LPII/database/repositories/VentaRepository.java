package com.domoticsweb.proy_appweb_LPII.database.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.domoticsweb.proy_appweb_LPII.database.entities.EstadoVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.domoticsweb.proy_appweb_LPII.database.entities.Usuario;
import com.domoticsweb.proy_appweb_LPII.database.entities.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {
	// Para mostrar el historial al usuario logueado (Ordenado por fecha)
    List<Venta> findByUsuarioOrderByFechaVentaDesc(Usuario usuario);

	// Suma total de ventas (excluye canceladas)
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.estado != 'CANCELADO'")
    BigDecimal sumTotalVentasPagadas();

    // Cuenta ventas (excluye canceladas)
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.estado != 'CANCELADO'")
    int countVentasPagadas();

	// Contar por estado (para el dashboard)
    long countByEstado(EstadoVenta estado);

	@Query("SELECT v FROM Venta v " +
			"LEFT JOIN v.usuario u " +
			"WHERE (:estado IS NULL OR v.estado = :estado) " +
			"AND (:cliente IS NULL OR :cliente = '' OR " +
			"     LOWER(u.nombreUsuario) LIKE LOWER(CONCAT('%', :cliente, '%')) OR " +
			"     LOWER(u.correo) LIKE LOWER(CONCAT('%', :cliente, '%'))) " +
			"AND (:fechaDesde IS NULL OR v.fechaVenta >= :fechaDesde) " +
			"AND (:fechaHasta IS NULL OR v.fechaVenta <= :fechaHasta) " +
			"ORDER BY " +
			"CASE WHEN :orden = 'fecha_desc' THEN v.fechaVenta END DESC, " +
			"CASE WHEN :orden = 'fecha_asc' THEN v.fechaVenta END ASC, " +
			"CASE WHEN :orden = 'monto_desc' THEN v.total END DESC, " +
			"CASE WHEN :orden = 'monto_asc' THEN v.total END ASC, " +
			"v.fechaVenta DESC")
    	List<Venta> filtrarPedidos(
    	    @Param("estado") EstadoVenta estado,
    	    @Param("cliente") String cliente,
    	    @Param("fechaDesde") LocalDateTime fechaDesde,
    	    @Param("fechaHasta") LocalDateTime fechaHasta,
    	    @Param("orden") String orden
    	);
}