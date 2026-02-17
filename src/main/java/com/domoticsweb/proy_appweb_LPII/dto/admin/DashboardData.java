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

    public String getNombreAdmin() { return nombreAdmin; }
    public void setNombreAdmin(String nombreAdmin) { this.nombreAdmin = nombreAdmin; }

    public String getNombreTienda() { return nombreTienda; }
    public void setNombreTienda(String nombreTienda) { this.nombreTienda = nombreTienda; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public BigDecimal getTotalVendido() { return totalVendido; }
    public void setTotalVendido(BigDecimal totalVendido) { this.totalVendido = totalVendido; }

    public int getCantidadVentas() { return cantidadVentas; }
    public void setCantidadVentas(int cantidadVentas) { this.cantidadVentas = cantidadVentas; }

    public BigDecimal getTicketPromedio() { return ticketPromedio; }
    public void setTicketPromedio(BigDecimal ticketPromedio) { this.ticketPromedio = ticketPromedio; }

    public int getNotificacionesBajoStock() { return notificacionesBajoStock; }
    public void setNotificacionesBajoStock(int notificacionesBajoStock) { this.notificacionesBajoStock = notificacionesBajoStock; }

    public List<String> getLabelsVentas() { return labelsVentas; }
    public void setLabelsVentas(List<String> labelsVentas) { this.labelsVentas = labelsVentas; }

    public List<BigDecimal> getSerieVentas() { return serieVentas; }
    public void setSerieVentas(List<BigDecimal> serieVentas) { this.serieVentas = serieVentas; }

    public List<Map<String, Object>> getTopProductos() { return topProductos; }
    public void setTopProductos(List<Map<String, Object>> topProductos) { this.topProductos = topProductos; }
}

