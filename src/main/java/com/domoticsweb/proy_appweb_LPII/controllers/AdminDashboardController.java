package com.domoticsweb.proy_appweb_LPII.controllers;

import com.domoticsweb.proy_appweb_LPII.database.entities.Usuario;
import com.domoticsweb.proy_appweb_LPII.database.repositories.RolRepository;
import com.domoticsweb.proy_appweb_LPII.database.repositories.UsuarioRepository;
import com.domoticsweb.proy_appweb_LPII.dto.admin.DashboardData;
import com.domoticsweb.proy_appweb_LPII.services.AdminDashboardService;
import com.domoticsweb.proy_appweb_LPII.services.InformePDFService;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import java.time.LocalDate;

@Controller
@AllArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final InformePDFService informePDFService;
    
    @GetMapping("/admin/panel")
    public String panelAdmin(Authentication authentication, Model model) {
        String username = authentication.getName(); // gerald o mary
        DashboardData data = dashboardService.obtenerDashboard(username);
        model.addAttribute("data", data);
        return "admin/dashboard";
    }

    @GetMapping("/admin/usuarios")
    public String usuarios(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long idRol,
            @RequestParam(required = false) Boolean activo,
            Model model) {

        // Normalizar parámetros vacíos a null
        nombre = (nombre != null && nombre.trim().isEmpty()) ? null : nombre;

        List<Usuario> usuarios;

        if (nombre != null || idRol != null || activo != null) {
            usuarios = usuarioRepository.filtrarUsuarios(nombre, idRol, activo);
        } else {
            usuarios = usuarioRepository.findAll();
        }

        // Obtener usuario actual (para evitar que se edite a sí mismo)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuarioActual = usuarioRepository.findByNombreUsuarioIgnoreCase(username).orElse(null);

        model.addAttribute("usuarioActual", usuarioActual);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("todosLosRoles", rolRepository.findAll());
        model.addAttribute("nombreFiltro", nombre);
        model.addAttribute("rolSeleccionado", idRol);
        model.addAttribute("activoSeleccionado", activo);

        return "admin/usuarios";
    }
    @GetMapping("/admin/informe/ventas-7-dias")
    public ResponseEntity<byte[]> descargarInformeVentas7Dias() {
        try {
            byte[] pdfBytes = informePDFService.generarInformeVentas7Dias();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("informe_ventas_7dias_" + LocalDate.now() + ".pdf")
                    .build());
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
