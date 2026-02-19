package com.domoticsweb.proy_appweb_LPII.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.domoticsweb.proy_appweb_LPII.database.entities.Categoria;
import com.domoticsweb.proy_appweb_LPII.database.entities.Producto;
import com.domoticsweb.proy_appweb_LPII.database.entities.ProductoImagen;
import com.domoticsweb.proy_appweb_LPII.database.repositories.ProductoRepository;
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
    private final ProductoRepository productoRepo;

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
    @GetMapping("/admin/productos")
    public String listarProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) Boolean activo,
            Model model,
            HttpServletRequest request) {

        List<Producto> productos;

        // Si hay algún filtro activo, usa el método de filtrado combinado
        if (nombre != null || idCategoria != null || activo != null) {
            productos = productoService.filtrarProductos(nombre, idCategoria, activo);
        } else {
            productos = productoRepo.findAll();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("nombreFiltro", nombre);
        model.addAttribute("categoriaSeleccionada", idCategoria);
        model.addAttribute("activoSeleccionado", activo);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/productos :: productosGrid";
        }

        return "admin/productos";
    }
    @PostMapping("/admin/productos/guardar")
    public String guardarProducto(

            @ModelAttribute Producto producto,
            @RequestParam Long categoriaId,
            @RequestParam(required = false) String urlImagen,
            @RequestParam(required = false) Integer stockInicial,
            @RequestParam(required = false) Integer stockMinimo
    ) {

        Categoria categoria = categoriaService.buscarPorId(categoriaId);

        producto.setCategoria(categoria);

        Producto prodGuardado =
                productoService.crearProducto(producto, stockInicial, stockMinimo);

        if (urlImagen != null && !urlImagen.isBlank()) {

            ProductoImagen img = ProductoImagen.builder()
                    .urlImagen(urlImagen)
                    .principal(true)
                    .producto(prodGuardado)
                    .build();

            productoService.agregarImagenes(
                    prodGuardado.getIdProducto(),
                    Set.of(img)
            );
        }

        return "redirect:/admin/productos";
    }

    @PostMapping("/admin/productos/actualizar/{id}")
    public String actualizarProducto(@PathVariable Long id,@ModelAttribute Producto producto,@RequestParam String urlImagen) {

        Producto actualizado = productoService.actualizarProducto(producto);

        if (urlImagen != null && !urlImagen.isBlank()) {

            ProductoImagen img = ProductoImagen.builder()
                    .urlImagen(urlImagen)
                    .principal(true)
                    .producto(actualizado)
                    .build();

            productoService.agregarImagenes(id, Set.of(img));
        }

        return "redirect:/admin/productos";
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

        Producto actualizado = productoService.actualizarProducto(producto);
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
    @PostMapping("/admin/productos/suspender/{id}")
    public String suspenderProducto(@PathVariable Long id){
        productoService.suspender(id);
        return "redirect:/admin/productos";
    }
}