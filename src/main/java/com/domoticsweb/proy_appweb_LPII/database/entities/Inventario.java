package com.domoticsweb.proy_appweb_LPII.database.entities;

import jakarta.persistence.*;
import lombok.Builder;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idInventario")
    private Long idInventario;

    @NotNull
    @Min(0)
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @NotNull
    @Min(0)
    @Column(name = "stockMinimo", nullable = false)
    private Integer stockMinimo;

    @OneToOne
    @JoinColumn(name = "idProducto", nullable = false, unique = true)
    private Producto producto;
}