package com.domoticsweb.proy_appweb_LPII.database.repositories.admin;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public class AdminDashboardRepository {

    private final JdbcTemplate jdbc;

    public AdminDashboardRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public BigDecimal totalVendido() {
        BigDecimal v = jdbc.queryForObject(
                "SELECT COALESCE(SUM(total),0) FROM ventas WHERE estado='PAGADA'",
                BigDecimal.class
        );
        return v == null ? BigDecimal.ZERO : v;
    }

    public int cantidadVentas() {
        Integer v = jdbc.queryForObject(
                "SELECT COUNT(*) FROM ventas WHERE estado='PAGADA'",
                Integer.class
        );
        return v == null ? 0 : v;
    }

    public int notificacionesBajoStock() {
        Integer v = jdbc.queryForObject(
                "SELECT COUNT(*) FROM inventario WHERE stock <= stockMinimo",
                Integer.class
        );
        return v == null ? 0 : v;
    }

    public List<Map<String, Object>> topProductos() {
        return jdbc.queryForList("""
            SELECT p.nombre AS nombre, COALESCE(SUM(dv.cantidad),0) AS cantidad
            FROM detalle_venta dv
            JOIN productos p ON p.idProducto = dv.idProducto
            GROUP BY p.idProducto, p.nombre
            ORDER BY cantidad DESC
            LIMIT 5
        """);
    }

    public List<Map<String, Object>> ventasUltimos7Dias() {
        return jdbc.queryForList("""
            SELECT DATE(v.fechaVenta) AS dia, COALESCE(SUM(v.total),0) AS total
            FROM ventas v
            WHERE v.estado='PAGADO'
              AND v.fechaVenta >= DATE_SUB(CURDATE(), INTERVAL 6 DAY)
            GROUP BY DATE(v.fechaVenta)
            ORDER BY dia
        """);
    }
}
