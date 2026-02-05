package com.domoticsweb.proy_appweb_LPII.controllers;

import java.util.List;

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

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    // Listar todas
    @GetMapping
    public List<Categoria> listar() {
        return categoriaService.listarTodas();
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public Categoria buscar(@PathVariable Long id) {
        return categoriaService.buscarPorId(id);
    }

    // Crear
    @PostMapping
    public Categoria crear(@RequestBody Categoria categoria) {
        return categoriaService.guardar(categoria);
    }

    // Actualizar
    @PutMapping("/{id}")
    public Categoria actualizar(@PathVariable Long id,
                                @RequestBody Categoria categoria) {
        return categoriaService.actualizar(id, categoria);
    }

    // Desactivar
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        categoriaService.desactivar(id);
    }
}
