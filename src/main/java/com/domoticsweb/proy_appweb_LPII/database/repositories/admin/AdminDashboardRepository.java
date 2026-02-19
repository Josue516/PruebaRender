package com.domoticsweb.proy_appweb_LPII.database.repositories.admin;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AdminDashboardRepository {

    private final JdbcTemplate jdbc;

    public AdminDashboardRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public int notificacionesBajoStock() {
        Integer v = jdbc.queryForObject(
                "SELECT COUNT(*) FROM inventario WHERE stock <= stockMinimo",
                Integer.class
        );
        return v == null ? 0 : v;
    }
 // NUEVO MÉTODO - Obtener detalles de productos con stock bajo
    public List<Map<String, Object>> productosBajoStock() {
        return jdbc.queryForList("""
            SELECT 
                p.idProducto,
                p.nombre,
                i.stock,
                i.stockMinimo,
                (i.stockMinimo - i.stock) AS diferencia
            FROM productos p
            INNER JOIN inventario i ON p.idProducto = i.idProducto
            WHERE i.stock <= i.stockMinimo
            ORDER BY i.stock ASC, p.nombre
        """);
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

    public List<Map<String, Object>> ventasUltimos7DiasPorEstado() {
        return jdbc.queryForList("""
            SELECT 
                DATE(v.fechaVenta) AS dia,
                v.estado,
                COALESCE(SUM(v.total), 0) AS total
            FROM ventas v
            WHERE v.estado != 'CANCELADO'
              AND v.fechaVenta >= DATE_SUB(CURDATE(), INTERVAL 6 DAY)
            GROUP BY DATE(v.fechaVenta), v.estado
            ORDER BY dia, v.estado
        """);
    }
}
