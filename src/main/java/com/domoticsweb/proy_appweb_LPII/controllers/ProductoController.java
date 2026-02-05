package com.domoticsweb.proy_appweb_LPII.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.domoticsweb.proy_appweb_LPII.database.entities.Producto;
import com.domoticsweb.proy_appweb_LPII.database.entities.ProductoImagen;
import com.domoticsweb.proy_appweb_LPII.services.ProductoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // Listar productos
    @GetMapping
    public List<Producto> listar() {
        return productoService.listarTodos();
    }

    // Buscar producto
    @GetMapping("/{id}")
    public Producto buscar(@PathVariable Long id) {
        return productoService.buscarPorId(id);
    }

    // Crear producto completo
    @PostMapping
    public Producto crearProducto(
            @RequestBody Producto producto,
            @RequestParam Integer stockInicial,
            @RequestParam Integer stockMinimo) {

        return productoService.crearProducto(producto, stockInicial, stockMinimo);
    }

    // Agregar imágenes
    @PostMapping("/{id}/imagenes")
    public Producto agregarImagenes(@PathVariable Long id,
                                    @RequestBody Set<ProductoImagen> imagenes) {
        return productoService.agregarImagenes(id, imagenes);
    }

    // Actualizar producto
    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id,
                               @RequestBody Producto producto) {
        return productoService.actualizarProducto(id, producto);
    }

    // Desactivar producto
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        productoService.desactivarProducto(id);
    }
}
