package com.domoticsweb.proy_appweb_LPII.database.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productos")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProducto;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 50)
    private String marca;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    // Relación con Categoría
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "idCategoria", nullable = false)
    private Categoria categoria;
    
    @Column(name = "fechaCreacion", nullable = false, updatable = false, 
            insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaCreacion;

    @Column(name = "fechaActualizacion", nullable = false, updatable = false, 
            insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime fechaActualizacion;

    @OneToOne(mappedBy = "producto", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Inventario inventario;
    
    @Builder.Default
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProductoImagen> imagenes = new ArrayList<>();
}