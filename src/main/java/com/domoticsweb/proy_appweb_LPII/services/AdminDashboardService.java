package com.domoticsweb.proy_appweb_LPII.services;

import com.domoticsweb.proy_appweb_LPII.database.entities.EstadoVenta;
import com.domoticsweb.proy_appweb_LPII.database.repositories.VentaRepository;
import com.domoticsweb.proy_appweb_LPII.database.repositories.admin.AdminDashboardRepository;
import com.domoticsweb.proy_appweb_LPII.dto.admin.DashboardData;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@AllArgsConstructor
public class AdminDashboardService {

    private final AdminDashboardRepository repo;
    private final VentaRepository ventaRepository;

    public DashboardData obtenerDashboard(String username) {
        DashboardData d = new DashboardData();

        d.setNombreAdmin(capitalizar(username));
        d.setNombreTienda("IEoDomoTics");
        d.setFotoUrl(null);

        BigDecimal total = ventaRepository.sumTotalVentasPagadas();
        int ventas = ventaRepository.countVentasPagadas();
        BigDecimal ticket = ventas > 0
                ? total.divide(BigDecimal.valueOf(ventas), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        d.setTotalVendido(total);
        d.setCantidadVentas(ventas);
        d.setTicketPromedio(ticket);
        d.setNotificacionesBajoStock(repo.notificacionesBajoStock());
        d.setProductosBajoStock(repo.productosBajoStock());

        // ========== PROCESAR VENTAS POR ESTADO ==========
        List<Map<String, Object>> filas = repo.ventasUltimos7DiasPorEstado();
        
        // Estructura: dia -> estado -> total
        Map<String, Map<EstadoVenta, BigDecimal>> ventasPorDiaYEstado = new LinkedHashMap<>();
        
        for (Map<String, Object> f : filas) {
            String dia = String.valueOf(f.get("dia"));
            EstadoVenta estado = EstadoVenta.valueOf(String.valueOf(f.get("estado")));
            Object t = f.get("total");
            BigDecimal totalDia = (t instanceof BigDecimal bd) ? bd : new BigDecimal(String.valueOf(t));
            
            ventasPorDiaYEstado
                .computeIfAbsent(dia, k -> new HashMap<>())
                .put(estado, totalDia);
        }
        
        // Obtener todos los días únicos (labels)
        List<String> labels = new ArrayList<>(ventasPorDiaYEstado.keySet());
        
        // Crear series para cada estado
        List<BigDecimal> seriePagado = new ArrayList<>();
        List<BigDecimal> serieEnPreparacion = new ArrayList<>();
        List<BigDecimal> serieEnviado = new ArrayList<>();
        List<BigDecimal> serieEntregado = new ArrayList<>();

        for (String dia : labels) {

            Map<EstadoVenta, BigDecimal> estadosDia =
                    ventasPorDiaYEstado.getOrDefault(dia, new EnumMap<>(EstadoVenta.class));

            seriePagado.add(estadosDia.getOrDefault(EstadoVenta.PAGADO, BigDecimal.ZERO));
            serieEnPreparacion.add(estadosDia.getOrDefault(EstadoVenta.EN_PREPARACION, BigDecimal.ZERO));
            serieEnviado.add(estadosDia.getOrDefault(EstadoVenta.DESPACHADO, BigDecimal.ZERO));
            serieEntregado.add(estadosDia.getOrDefault(EstadoVenta.ENTREGADO, BigDecimal.ZERO));
        }
        
        // Calcular serie total (para mantener compatibilidad)
        List<BigDecimal> serieTotal = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            BigDecimal totalDelDia = seriePagado.get(i)
                .add(serieEnPreparacion.get(i))
                .add(serieEnviado.get(i))
                .add(serieEntregado.get(i));
            serieTotal.add(totalDelDia);
        }
        
        d.setLabelsVentas(labels);
        d.setSerieVentas(serieTotal);
        d.setSeriePagado(seriePagado);
        d.setSerieEnPreparacion(serieEnPreparacion);
        d.setSerieEnviado(serieEnviado);
        d.setSerieEntregado(serieEntregado);
        
        d.setTopProductos(repo.topProductos());

        return d;
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isBlank()) return "";
        return texto.substring(0,1).toUpperCase() + texto.substring(1).toLowerCase();
    }
}
