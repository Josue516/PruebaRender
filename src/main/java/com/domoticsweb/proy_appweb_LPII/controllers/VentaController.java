package com.domoticsweb.proy_appweb_LPII.controllers;

import com.domoticsweb.proy_appweb_LPII.database.entities.*;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.domoticsweb.proy_appweb_LPII.database.repositories.DetalleVentaRepository;
import com.domoticsweb.proy_appweb_LPII.database.repositories.ProductoRepository;
import com.domoticsweb.proy_appweb_LPII.database.repositories.UsuarioRepository;
import com.domoticsweb.proy_appweb_LPII.database.repositories.VentaRepository;
import com.domoticsweb.proy_appweb_LPII.dto.CarritoDTO;
import com.domoticsweb.proy_appweb_LPII.dto.VentaDTO;
import com.domoticsweb.proy_appweb_LPII.services.InventarioService;
import com.domoticsweb.proy_appweb_LPII.services.PaypalService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioService inventarioService;
    private final PaypalService payPalService; 

    @PostMapping("/finalizar")
    @Transactional
    public ResponseEntity<?> finalizarCompra(@RequestBody VentaDTO compra, Authentication auth) {
        try {
            // ========== VERIFICAR PAGO DE PAYPAL ==========
            if (!payPalService.verificarPago(compra.getOrderId())) {
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                        .body(Map.of("error", "El pago no fue verificado"));
            }
            
            // ========== IDENTIFICAR AL USUARIO ==========
            Usuario usuario = usuarioRepository.findByNombreUsuarioIgnoreCase(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + auth.getName()));

            // ========== VALIDAR STOCK ANTES DE PROCESAR ==========
            for (CarritoDTO item : compra.getItems()) {
                Producto producto = productoRepository.findById(item.getId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                
                Inventario inventario = producto.getInventario();
                
                // Validar que haya stock suficiente
                if (inventario.getStock() < item.getCantidad()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of(
                                "error", "Stock insuficiente",
                                "mensaje", "El producto: '" + producto.getNombre() + "' tiene " +
                                          "Disponible: " + inventario.getStock() + ", " +
                                          "Solicitado: " + item.getCantidad()));}}
            if (usuario.getDireccion() == null || usuario.getDireccion().isBlank() ||
            	    usuario.getNumero() == null || usuario.getNumero().isBlank()) {

            	    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            	            .body(Map.of(
            	                "code", "DATOS_ENVIO_INCOMPLETOS",
            	                "mensaje", "Debes completar tu dirección y teléfono antes de realizar la compra."));
            	}

            // ========== CREAR VENTA ==========
            Venta venta = new Venta();
            venta.setUsuario(usuario);
            venta.setFechaVenta(LocalDateTime.now());
            venta.setEstado(EstadoVenta.PAGADO);

            // Calcular total
            double total = compra.getItems().stream()
                    .mapToDouble(item -> item.getPrecio() * item.getCantidad())
                    .sum();
            venta.setTotal(total);

            Venta nuevaVenta = ventaRepository.save(venta);

            // ========== CREAR DETALLES DE VENTA ==========
            List<DetalleVenta> detalles = compra.getItems().stream().map((CarritoDTO item) -> {
                Producto producto = productoRepository.findById(item.getId())
                        .orElseThrow(() -> new RuntimeException("Producto ID " + item.getId() + " no existe"));

                DetalleVenta detalle = new DetalleVenta();
                detalle.setVenta(nuevaVenta);
                detalle.setProducto(producto);
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioUnitario(item.getPrecio());
                detalle.setSubtotal(item.getPrecio() * item.getCantidad());

                return detalle;
            }).collect(Collectors.toList());

            detalleVentaRepository.saveAll(detalles);

            // ========== REDUCIR STOCK ==========
            for (CarritoDTO item : compra.getItems()) {
                inventarioService.reducirStock(item.getId(), item.getCantidad());
            }

            return ResponseEntity.ok(Map.of(
                "idVenta", nuevaVenta.getIdVenta(),
                "mensaje", "✅ ¡Compra realizada con éxito! Tu pedido ha sido confirmado."
            ));

        } catch (RuntimeException e) {
            // Capturar errores específicos de stock del InventarioService
            String errorMsg = e.getMessage();
            // Error genérico de RuntimeException
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "error", "Error al procesar la compra",
                        "mensaje", errorMsg
                    ));
        } catch (Exception e) {
            // Error inesperado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error inesperado",
                        "mensaje", "Ocurrió un error al procesar tu compra. Por favor, intenta nuevamente."
                    ));
        }
    }
    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuth(Authentication auth) {
        // Si auth es null o no está autenticado, Spring Security suele manejarlo,
        // pero aquí confirmamos explícitamente.
        if (auth != null && auth.isAuthenticated()) {
            return ResponseEntity.ok(Map.of("autenticado", true));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}