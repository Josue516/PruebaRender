package com.domoticsweb.proy_appweb_LPII.database.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ventas")
@Data
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVenta;

    @Column(name = "fechaVenta", nullable = false, updatable = false)
    private LocalDateTime fechaVenta = LocalDateTime.now();

    @Column(nullable = false)
    private Double total;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private EstadoVenta estado = EstadoVenta.PAGADO;

    // Relación con el Usuario (Cliente)
    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    // Relación inversa para poder ver los detalles desde la venta si lo necesitas
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    private List<DetalleVenta> detalles;
}
