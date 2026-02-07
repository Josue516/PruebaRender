package com.domoticsweb.proy_appweb_LPII.database.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "producto_imagenes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoImagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idImagen")
    private Long idImagen;

    @Column(name = "urlImagen", nullable = false, length = 500)
    private String urlImagen;
    
    @Builder.Default
    @Column(name = "principal", nullable = false)
    private Boolean principal = false;

    @ManyToOne
    @JoinColumn(name = "idProducto", nullable = false)
    @JsonBackReference
    private Producto producto;
    
}
