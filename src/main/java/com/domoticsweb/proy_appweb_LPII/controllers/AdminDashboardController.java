package com.domoticsweb.proy_appweb_LPII.controllers;

import com.domoticsweb.proy_appweb_LPII.dto.admin.DashboardData;
import com.domoticsweb.proy_appweb_LPII.services.AdminDashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

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

    @GetMapping("/admin/categorias")
    public String categorias() {
        return "admin/categorias";
    }

    @GetMapping("/admin/productos")
    public String productos() {
        return "admin/productos";
    }

    @GetMapping("/admin/stock")
    public String stock() {
        return "admin/stock";
    }
}
