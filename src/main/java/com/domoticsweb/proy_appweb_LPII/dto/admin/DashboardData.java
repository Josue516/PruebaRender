package com.domoticsweb.proy_appweb_LPII.dto.admin;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class DashboardData {
    private String nombreAdmin;
    private String nombreTienda;
    private String fotoUrl;

    private BigDecimal totalVendido;
    private int cantidadVentas;
    private BigDecimal ticketPromedio;

    private int notificacionesBajoStock;

    private List<String> labelsVentas;
    private List<BigDecimal> serieVentas;
    
 // NUEVOS CAMPOS PARA MÚLTIPLES ESTADOS
    private List<BigDecimal> seriePagado;
    private List<BigDecimal> serieEnPreparacion;
    private List<BigDecimal> serieEnviado;
    private List<BigDecimal> serieEntregado;

    private List<Map<String, Object>> topProductos; // [{nombre, cantidad}]

}

