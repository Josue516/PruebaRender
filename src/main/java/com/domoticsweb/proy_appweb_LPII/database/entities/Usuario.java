package com.domoticsweb.proy_appweb_LPII.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario")
    private Long idUsuario;

    @Column(name = "nombreUsuario", nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    @Column(name = "correo", nullable = false, unique = true, length = 120)
    private String correo;

    @Column(name = "contrasenaHash", nullable = false, length = 255)
    private String contrasenaHash;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    // AGREGUE ESTOS CAMPOS PARA MAPEARLOS POR LOS REPORTES, PERO SON SOLO MANEJADOS POR LA BASE DE DATOS.

    @Column(name = "fechaCreacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fechaActualizacion", insertable = false, updatable = false)
    private LocalDateTime fechaActualizacion;

    // ROLES 
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_roles",
            joinColumns = @JoinColumn(name = "idUsuario"),
            inverseJoinColumns = @JoinColumn(name = "idRol")
    )
    private Set<Rol> roles = new HashSet<>();
}
