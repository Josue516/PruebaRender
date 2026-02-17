package com.domoticsweb.proy_appweb_LPII.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.domoticsweb.proy_appweb_LPII.database.entities.Venta;
import com.domoticsweb.proy_appweb_LPII.database.repositories.VentaRepository;
import com.domoticsweb.proy_appweb_LPII.services.InventarioService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/gestor")
@AllArgsConstructor
@PreAuthorize("hasRole('GESTOR') or hasRole('ADMIN')") // Solo gestores y admins
public class GestorController {

    private final VentaRepository ventaRepository;
    private final InventarioService inventarioService;
    @GetMapping  
    public String panel() {
        return "redirect:/gestor/pedidos";  // Redirige directo a pedidos
    }

    @GetMapping("/pedidos")
    public String pedidos(
        @RequestParam(required = false) String estado,
        @RequestParam(required = false) String cliente,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
        @RequestParam(required = false, defaultValue = "fecha_desc") String orden,
        Model model
    ) {
        // Convertir LocalDate a LocalDateTime (inicio del día / fin del día)
        LocalDateTime fechaDesdeTime = (fechaDesde != null) ? fechaDesde.atStartOfDay() : null;
        LocalDateTime fechaHastaTime = (fechaHasta != null) ? fechaHasta.atTime(23, 59, 59) : null;
        
        // Estadísticas
        model.addAttribute("pendientes", ventaRepository.countByEstado("PAGADO"));
        model.addAttribute("enPreparacion", ventaRepository.countByEstado("EN_PREPARACION"));
        model.addAttribute("enviados", ventaRepository.countByEstado("ENVIADO"));

        // Lista de pedidos (con todos los filtros)
        List<Venta> ventas = ventaRepository.filtrarPedidos(estado, cliente, fechaDesdeTime, fechaHastaTime, orden);
        model.addAttribute("ventas", ventas);

        return "gestor/pedidos";
    }
    // CAMBIAR ESTADO DE PEDIDO
    @PostMapping("/pedidos/cambiar-estado/{id}")
    public String cambiarEstadoPedido(
            @PathVariable Long id,
            @RequestParam String nuevoEstado,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @RequestParam(required = false, defaultValue = "fecha_desc") String orden,
            RedirectAttributes redirectAttributes) {

        try {
            Venta venta = ventaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

            String estadoAnterior = venta.getEstado();

            // ========== VALIDACIÓN 1: Estados finales no pueden cambiar ==========
            if (estadoAnterior.equals("ENTREGADO")) {
                redirectAttributes.addFlashAttribute("error", 
                    "❌ No se puede modificar un pedido que ya fue ENTREGADO");
                preservarFiltros(estado, cliente, fechaDesde, fechaHasta, orden, redirectAttributes);
                return "redirect:/gestor/pedidos";
            }

            if (estadoAnterior.equals("CANCELADO")) {
                redirectAttributes.addFlashAttribute("error", 
                    "❌ No se puede modificar un pedido CANCELADO");
                preservarFiltros(estado, cliente, fechaDesde, fechaHasta, orden, redirectAttributes);
                return "redirect:/gestor/pedidos";
            }

            // ========== VALIDACIÓN 2: No retroceder estados ==========
            if (!puedeAvanzarA(estadoAnterior, nuevoEstado)) {
                redirectAttributes.addFlashAttribute("error", 
                    "❌ No se puede cambiar de " + estadoAnterior + " a " + nuevoEstado + 
                    ". Solo se puede avanzar en el flujo o cancelar.");
                preservarFiltros(estado, cliente, fechaDesde, fechaHasta, orden, redirectAttributes);
                return "redirect:/gestor/pedidos";
            }

            // ========== VALIDACIÓN 3: No cancelar si ya fue enviado o entregado ==========
            if (nuevoEstado.equals("CANCELADO")) {
                if (estadoAnterior.equals("ENVIADO") || estadoAnterior.equals("ENTREGADO")) {
                    redirectAttributes.addFlashAttribute("error", 
                        "❌ No se puede cancelar un pedido que ya fue enviado o entregado");
                    preservarFiltros(estado, cliente, fechaDesde, fechaHasta, orden, redirectAttributes);
                    return "redirect:/gestor/pedidos";
                }
                
                // ========== RESTAURAR STOCK AL CANCELAR ==========
                inventarioService.restaurarStockVenta(venta.getDetalles());
            }

            // ========== ACTUALIZAR ESTADO ==========
            venta.setEstado(nuevoEstado);
            ventaRepository.save(venta);

            // Preservar filtros
            preservarFiltros(estado, cliente, fechaDesde, fechaHasta, orden, redirectAttributes);

            // Mensaje de éxito
            String mensaje = "Estado del pedido #" + id + " actualizado";
            if (nuevoEstado.equals("CANCELADO")) {
                mensaje += " (Stock restaurado)";
            }
            
            redirectAttributes.addFlashAttribute("mensaje", mensaje);
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
            preservarFiltros(estado, cliente, fechaDesde, fechaHasta, orden, redirectAttributes);
        }

        return "redirect:/gestor/pedidos";
    }
    private boolean puedeAvanzarA(String estadoActual, String nuevoEstado) {
        // Mismo estado = permitido (aunque no hace nada)
        if (estadoActual.equals(nuevoEstado)) {
            return true;
        }
        
        switch (estadoActual) {
            case "PAGADO":
                return nuevoEstado.equals("EN_PREPARACION") || 
                       nuevoEstado.equals("CANCELADO");
                
            case "EN_PREPARACION":
                return nuevoEstado.equals("ENVIADO") || 
                       nuevoEstado.equals("CANCELADO");
                
            case "ENVIADO":
                return nuevoEstado.equals("ENTREGADO");
                // NO puede cancelar desde ENVIADO
                
            case "ENTREGADO":
            case "CANCELADO":
                return false; // Estados finales, no pueden cambiar
                
            default:
                return false;
        }
    }

    // Método auxiliar para no repetir código
    private void preservarFiltros(String estado, String cliente, String fechaDesde, 
                                   String fechaHasta, String orden, 
                                   RedirectAttributes redirectAttributes) {
        if (estado != null && !estado.isBlank()) {
            redirectAttributes.addAttribute("estado", estado);
        }
        if (cliente != null && !cliente.isBlank()) {
            redirectAttributes.addAttribute("cliente", cliente);
        }
        if (fechaDesde != null && !fechaDesde.isBlank()) {
            redirectAttributes.addAttribute("fechaDesde", fechaDesde);
        }
        if (fechaHasta != null && !fechaHasta.isBlank()) {
            redirectAttributes.addAttribute("fechaHasta", fechaHasta);
        }
        redirectAttributes.addAttribute("orden", orden);
    }
    @GetMapping("/pedidos/{id}/detalles")
    @ResponseBody
    public ResponseEntity<?> obtenerDetallesPedido(@PathVariable Long id) {
        try {
            Venta venta = ventaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            
            // Crear respuesta con los datos necesarios
            Map<String, Object> response = new HashMap<>();
            response.put("idVenta", venta.getIdVenta());
            response.put("fechaVenta", venta.getFechaVenta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            response.put("estado", venta.getEstado());
            response.put("total", venta.getTotal());
            
            // Datos del cliente
            Map<String, String> cliente = new HashMap<>();
            cliente.put("nombre", venta.getUsuario().getNombreUsuario());
            cliente.put("correo", venta.getUsuario().getCorreo());
            response.put("cliente", cliente);
            
            // Detalles de productos
            List<Map<String, Object>> productos = venta.getDetalles().stream().map(detalle -> {
                Map<String, Object> prod = new HashMap<>();
                prod.put("nombre", detalle.getProducto().getNombre());
                prod.put("cantidad", detalle.getCantidad());
                prod.put("precioUnitario", detalle.getPrecioUnitario());
                prod.put("subtotal", detalle.getSubtotal());
                return prod;
            }).collect(Collectors.toList());
            response.put("productos", productos);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}