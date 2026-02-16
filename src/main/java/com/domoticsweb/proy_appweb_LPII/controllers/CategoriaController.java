package com.domoticsweb.proy_appweb_LPII.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.domoticsweb.proy_appweb_LPII.database.entities.Categoria;
import com.domoticsweb.proy_appweb_LPII.services.CategoriaService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public String listarCategorias(Model model) {

        List<Categoria> categorias = categoriaService.listarTodas();
        model.addAttribute("categorias", categorias);

        System.out.println("Categorias encontradas: " + categorias.size());

        return "admin/categorias";
    }
    @PostMapping("/save")
    public String guardar(Categoria categoria){

        categoriaService.guardar(categoria);

        return "redirect:/admin/categorias";
    }

    @PostMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id){

        categoriaService.desactivar(id);

        return "redirect:/admin/categorias";
    }
    @PostMapping("/estado/{id}")
    public String cambiarEstado(@PathVariable Long id){
        categoriaService.desactivar(id);
        return "redirect:/admin/categorias";
    }
}
