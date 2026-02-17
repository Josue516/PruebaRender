package com.domoticsweb.proy_appweb_LPII.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.domoticsweb.proy_appweb_LPII.database.entities.Rol;
import com.domoticsweb.proy_appweb_LPII.database.entities.Usuario;
import com.domoticsweb.proy_appweb_LPII.database.repositories.RolRepository;
import com.domoticsweb.proy_appweb_LPII.database.repositories.UsuarioRepository;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/admin/usuarios")
@AllArgsConstructor
public class UsuarioAdminController {

    private final PasswordEncoder passwordEncoder;

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @PostMapping("/save")
    public String guardarUsuario(
            @ModelAttribute Usuario usuario,
            @RequestParam(required = false) String contrasenaRaw,
            @RequestParam(required = false) Long rolId) {

        if (usuario.getIdUsuario() == null) {
            // ===== NUEVO USUARIO =====
            if (contrasenaRaw != null && !contrasenaRaw.isBlank()) {
                usuario.setContrasenaHash(passwordEncoder.encode(contrasenaRaw));
            }

            usuario.setActivo(true);

        } else {
            // ===== EDICIÓN =====
            Usuario usuarioBD = usuarioRepository
                    .findById(usuario.getIdUsuario())
                    .orElseThrow();

            // Mantener contraseña anterior
            usuario.setContrasenaHash(usuarioBD.getContrasenaHash());
        }

        // ===== MANEJO DE ROLES =====
        if (rolId != null) {

            Rol rol = rolRepository.findById(rolId).orElseThrow();

            usuario.setRoles(Set.of(rol));
        }

        usuarioRepository.save(usuario);

        return "redirect:/admin/usuarios";
    }
    @GetMapping("/validar")
    @ResponseBody
    public Map<String, Boolean> validarUsuario(
            @RequestParam(required = false) String nombreUsuario,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) Integer idUsuario) {

        boolean usuarioExiste = false;
        boolean correoExiste = false;

        if (nombreUsuario != null && !nombreUsuario.isBlank()) {
            usuarioExiste = usuarioRepository
                    .existsByNombreUsuarioIgnoreCaseAndIdUsuarioNot(nombreUsuario, idUsuario == null ? -1 : idUsuario);
        }

        if (correo != null && !correo.isBlank()) {
            correoExiste = usuarioRepository
                    .existsByCorreoIgnoreCaseAndIdUsuarioNot(correo, idUsuario == null ? -1 : idUsuario);
        }

        Map<String, Boolean> resultado = new HashMap<>();
        resultado.put("usuarioExiste", usuarioExiste);
        resultado.put("correoExiste", correoExiste);

        return resultado;
    }
    @PostMapping("/suspender/{id}")
    public String cambiarEstadoUsuario(
            @PathVariable Long id,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long idRol,
            @RequestParam(required = false) Boolean activo,
            RedirectAttributes redirectAttributes,
            Authentication auth) {

        // Verificar que no intente suspenderse a sí mismo
        Usuario usuarioActual = usuarioRepository.findByNombreUsuarioIgnoreCase(auth.getName()).orElse(null);
        
        if (usuarioActual != null && usuarioActual.getIdUsuario().equals(id)) {
            redirectAttributes.addFlashAttribute("error", "No puedes cambiar tu propio estado");
            return "redirect:/admin/usuarios";
        }

        // Cambiar estado del usuario
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(!usuario.getActivo());
        usuarioRepository.save(usuario);

        // Mantener filtros
        if (nombre != null && !nombre.isBlank()) {
            redirectAttributes.addAttribute("nombre", nombre);
        }
        if (idRol != null) {
            redirectAttributes.addAttribute("idRol", idRol);
        }
        if (activo != null) {
            redirectAttributes.addAttribute("activo", activo);
        }

        return "redirect:/admin/usuarios";
    }
}