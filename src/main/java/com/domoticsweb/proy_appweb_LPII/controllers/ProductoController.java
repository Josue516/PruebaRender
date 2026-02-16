package com.domoticsweb.proy_appweb_LPII.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.domoticsweb.proy_appweb_LPII.database.entities.Producto;
import com.domoticsweb.proy_appweb_LPII.dto.ProductoDTO;
import com.domoticsweb.proy_appweb_LPII.dto.VentaDTO;
import com.domoticsweb.proy_appweb_LPII.services.CategoriaService;
import com.domoticsweb.proy_appweb_LPII.services.ProductoService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    @GetMapping("/productos")
    public String mostrarProductos(
            @RequestParam(required = false) Long idCat,
            Model model,
            HttpServletRequest request) {

        List<Producto> productos = (idCat != null)
                ? productoService.listarPorCategoria(idCat)
                : productoService.listarTodos();

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("categoriaSeleccionada", idCat != null ? idCat : "todos");

        // Petición AJAX → devolver solo el fragmento
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "pages/productos :: productosGrid";
        }

        return "pages/productos";
    }

    /* ==================== API REST (JSON) ==================== */

    @GetMapping("/api/productos")
    @ResponseBody
    public List<ProductoDTO> listarProductosJSON() {
        return productoService.listarTodos()
                .stream()
                .map(ProductoDTO::new)
                .toList();
    }
    
    @GetMapping("/api/productos/{id}")
    @ResponseBody
    public ResponseEntity<Producto> buscarProducto(@PathVariable Long id) {
        try {
            Producto producto = productoService.buscarPorId(id);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/productos")
    @ResponseBody
    public ResponseEntity<Producto> crearProducto(
            @RequestBody Producto producto,
            @RequestParam Integer stockInicial,
            @RequestParam Integer stockMinimo) {

        Producto nuevo = productoService.crearProducto(producto, stockInicial, stockMinimo);
        return ResponseEntity.ok(nuevo);
    }

    @PutMapping("/api/productos/{id}")
    @ResponseBody
    public ResponseEntity<Producto> actualizarProducto(
            @PathVariable Long id,
            @RequestBody Producto producto) {

        Producto actualizado = productoService.actualizarProducto(id, producto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/api/productos/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.desactivarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/productos/categoria/{idCategoria}")
    @ResponseBody
    public List<ProductoDTO> listarPorCategoriaJSON(@PathVariable Long idCategoria) {
        return productoService.listarPorCategoria(idCategoria)
                .stream()
                .map(ProductoDTO::new)
                .toList();
    }

    @GetMapping("/api/productos/buscar")
    @ResponseBody
    public List<ProductoDTO> buscarPorNombre(@RequestParam String q) {
        return productoService.buscarPorNombre(q)
                .stream()
                .map(ProductoDTO::new)
                .toList();
    }
    @PostMapping("/api/pedidos/confirmar")
    @ResponseBody
    public ResponseEntity<?> confirmarCompra(@RequestBody VentaDTO venta) {
        try {
            productoService.procesarVenta(venta);
            return ResponseEntity.ok("¡Pedido Completado!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}