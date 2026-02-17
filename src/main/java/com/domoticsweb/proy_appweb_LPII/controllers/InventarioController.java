package com.domoticsweb.proy_appweb_LPII.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.domoticsweb.proy_appweb_LPII.database.entities.Inventario;
import com.domoticsweb.proy_appweb_LPII.services.CategoriaService;
import com.domoticsweb.proy_appweb_LPII.services.InventarioService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;
    
    private final CategoriaService categoriaService;

    @GetMapping("/admin/stock")
    public String listarInventario(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) String estado,
            Model model) {

        // Normalizar parámetros vacíos a null
        nombre = (nombre != null && nombre.trim().isEmpty()) ? null : nombre;
        estado = (estado != null && estado.trim().isEmpty()) ? null : estado;

        List<Inventario> inventarios;

        if (nombre != null || idCategoria != null || estado != null) {
            inventarios = inventarioService.filtrarInventario(nombre, idCategoria, estado);
        } else {
            inventarios = inventarioService.listarTodos();
        }

        model.addAttribute("inventarios", inventarios);
        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("nombreFiltro", nombre);
        model.addAttribute("categoriaSeleccionada", idCategoria);
        model.addAttribute("estadoSeleccionado", estado);
        return "admin/stock";
    }

    @PostMapping("/admin/stock/actualizar/{id}")
    public String actualizarStock(
            @PathVariable Long id,
            @RequestParam Integer stock,
            @RequestParam Integer stockMinimo,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) String estado,
            RedirectAttributes redirectAttributes) {

        inventarioService.actualizarStock(id, stock, stockMinimo);

        // Mantener filtros
        if (nombre != null && !nombre.isBlank()) {
            redirectAttributes.addAttribute("nombre", nombre);
        }
        if (idCategoria != null) {
            redirectAttributes.addAttribute("idCategoria", idCategoria);
        }
        if (estado != null && !estado.isBlank()) {
            redirectAttributes.addAttribute("estado", estado);
        }

        return "redirect:/admin/stock";
    }
}
