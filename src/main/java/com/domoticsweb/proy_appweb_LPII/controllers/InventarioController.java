package com.domoticsweb.proy_appweb_LPII.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.domoticsweb.proy_appweb_LPII.database.entities.Inventario;
import com.domoticsweb.proy_appweb_LPII.services.InventarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    // Listar inventario
    @GetMapping
    public List<Inventario> listar() {
        return inventarioService.listarTodo();
    }

    // Buscar por producto
    @GetMapping("/producto/{idProducto}")
    public Inventario buscarPorProducto(@PathVariable Long idProducto) {
        return inventarioService.buscarPorProducto(idProducto);
    }

    // Aumentar stock
    @PutMapping("/aumentar/{idProducto}")
    public Inventario aumentarStock(@PathVariable Long idProducto,
                                    @RequestParam Integer cantidad) {
        return inventarioService.aumentarStock(idProducto, cantidad);
    }

    // Reducir stock
    @PutMapping("/reducir/{idProducto}")
    public Inventario reducirStock(@PathVariable Long idProducto,
                                   @RequestParam Integer cantidad) {
        return inventarioService.reducirStock(idProducto, cantidad);
    }

    // Actualizar stock mínimo
    @PutMapping("/minimo/{idProducto}")
    public Inventario actualizarMinimo(@PathVariable Long idProducto,
                                       @RequestParam Integer stockMinimo) {
        return inventarioService.actualizarStockMinimo(idProducto, stockMinimo);
    }
}
