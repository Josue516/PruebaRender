package com.domoticsweb.proy_appweb_LPII.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
	
	private Long id;
	private String nombreUsuario;
    private String correo;
    private String nuevaContrasena;
    private String direccion;
    private String numero;
}
