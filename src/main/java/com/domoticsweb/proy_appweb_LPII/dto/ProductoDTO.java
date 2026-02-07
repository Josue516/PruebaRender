package com.domoticsweb.proy_appweb_LPII.dto;

import java.math.BigDecimal;
import java.util.List;

import com.domoticsweb.proy_appweb_LPII.database.entities.Producto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO {

    private Long idProducto;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String marca;

    private CategoriaDTO categoria;
    private List<ProductoImagenDTO> imagenes;

    public ProductoDTO(Producto p) {
        this.idProducto = p.getIdProducto();
        this.nombre = p.getNombre();
        this.descripcion = p.getDescripcion();
        this.precio = p.getPrecio();
        this.marca = p.getMarca();

        if (p.getCategoria() != null) {
            this.categoria = new CategoriaDTO(p.getCategoria());
        }

        if (p.getImagenes() != null) {
            this.imagenes = p.getImagenes()
                    .stream()
                    .map(ProductoImagenDTO::new)
                    .toList();
        }
    }
}