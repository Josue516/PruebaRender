package com.domoticsweb.proy_appweb_LPII.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroRequest {
    private String nombreUsuario;
    private String correo;
    private String contrasena;
    private String direccion;
    private String numero;
}
