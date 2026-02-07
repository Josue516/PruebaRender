package com.domoticsweb.proy_appweb_LPII.dto;

import com.domoticsweb.proy_appweb_LPII.database.entities.ProductoImagen;

import lombok.Data;

@Data
public class ProductoImagenDTO {

    private String urlImagen;
    private Boolean principal;

    public ProductoImagenDTO(ProductoImagen img) {
        this.urlImagen = img.getUrlImagen();
        this.principal = img.getPrincipal();
    }
}