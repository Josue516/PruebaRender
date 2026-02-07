package com.domoticsweb.proy_appweb_LPII.dto;

import com.domoticsweb.proy_appweb_LPII.database.entities.Categoria;

import lombok.Data;

@Data
public class CategoriaDTO {

    private Long idCategoria;
    private String nombre;

    public CategoriaDTO(Categoria c) {
        this.idCategoria = c.getIdCategoria();
        this.nombre = c.getNombre();
    }
}