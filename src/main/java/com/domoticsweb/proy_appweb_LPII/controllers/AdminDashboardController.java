package com.domoticsweb.proy_appweb_LPII.controllers;

import com.domoticsweb.proy_appweb_LPII.database.entities.Usuario;
import com.domoticsweb.proy_appweb_LPII.database.repositories.RolRepository;
import com.domoticsweb.proy_appweb_LPII.database.repositories.UsuarioRepository;
import com.domoticsweb.proy_appweb_LPII.dto.admin.DashboardData;
import com.domoticsweb.proy_appweb_LPII.services.AdminDashboardService;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @GetMapping("/admin/panel")
    public String panelAdmin(Authentication authentication, Model model) {
        String username = authentication.getName(); // gerald o mary
        DashboardData data = dashboardService.obtenerDashboard(username);
        model.addAttribute("data", data);
        return "admin/dashboard";
    }

    @GetMapping("/admin/proveedores")
    public String proveedores() {
        return "admin/proveedores";
    }



    @GetMapping("/admin/productos")
    public String productos() {
        return "admin/productos";
    }

    @GetMapping("/admin/stock")
    public String stock() {
        return "admin/stock";
    }
    @GetMapping("/admin/usuarios")
    public String usuarios(Model model) {
    	List<Usuario> listaUsuarios = usuarioRepository.findAll();
    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // nombre del usuario logueado
        Usuario usuarioActual = usuarioRepository.findByNombreUsuarioIgnoreCase(username).orElse(null);
        model.addAttribute("usuarioActual", usuarioActual);
        model.addAttribute("todosLosRoles", rolRepository.findAll());
        model.addAttribute("usuarios", listaUsuarios);
        return "admin/usuarios";
    }
}
