package com.domoticsweb.proy_appweb_LPII.services;

import com.domoticsweb.proy_appweb_LPII.database.repositories.admin.AdminDashboardRepository;
import com.domoticsweb.proy_appweb_LPII.dto.admin.DashboardData;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AdminDashboardService {

    private final AdminDashboardRepository repo;

    public AdminDashboardService(AdminDashboardRepository repo) {
        this.repo = repo;
    }

    public DashboardData obtenerDashboard(String username) {
        DashboardData d = new DashboardData();

        d.setNombreAdmin(capitalizar(username));
        d.setNombreTienda("IEoDomoTics");
        d.setFotoUrl(null); // luego puedes poner /images/mary.jpg, etc.

        BigDecimal total = repo.totalVendido();
        int ventas = repo.cantidadVentas();
        BigDecimal ticket = ventas > 0
                ? total.divide(BigDecimal.valueOf(ventas), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        d.setTotalVendido(total);
        d.setCantidadVentas(ventas);
        d.setTicketPromedio(ticket);
        d.setNotificacionesBajoStock(repo.notificacionesBajoStock());

        List<Map<String, Object>> filas = repo.ventasUltimos7Dias();
        List<String> labels = new ArrayList<>();
        List<BigDecimal> serie = new ArrayList<>();

        for (Map<String, Object> f : filas) {
            labels.add(String.valueOf(f.get("dia")));
            Object t = f.get("total");
            if (t instanceof BigDecimal bd) serie.add(bd);
            else serie.add(new BigDecimal(String.valueOf(t)));
        }

        d.setLabelsVentas(labels);
        d.setSerieVentas(serie);
        d.setTopProductos(repo.topProductos());

        return d;
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isBlank()) return "";
        return texto.substring(0,1).toUpperCase() + texto.substring(1).toLowerCase();
    }
}
