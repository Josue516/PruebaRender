package com.domoticsweb.proy_appweb_LPII.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.domoticsweb.proy_appweb_LPII.database.entities.DetalleVenta;
import com.domoticsweb.proy_appweb_LPII.database.entities.Producto;
import com.domoticsweb.proy_appweb_LPII.database.entities.Usuario;
import com.domoticsweb.proy_appweb_LPII.database.entities.Venta;
import com.domoticsweb.proy_appweb_LPII.database.repositories.DetalleVentaRepository;
import com.domoticsweb.proy_appweb_LPII.database.repositories.ProductoRepository;
import com.domoticsweb.proy_appweb_LPII.database.repositories.UsuarioRepository;
import com.domoticsweb.proy_appweb_LPII.database.repositories.VentaRepository;
import com.domoticsweb.proy_appweb_LPII.dto.CarritoDTO;
import com.domoticsweb.proy_appweb_LPII.dto.VentaDTO;

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

    @PostMapping("/finalizar")
    @Transactional
    public ResponseEntity<?> finalizarCompra(@RequestBody VentaDTO compra, Authentication auth) {
        try {
            // 1. Identificar al usuario
            Usuario usuario = usuarioRepository.findByNombreUsuarioIgnoreCase(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + auth.getName()));

            // 2. Crear cabecera de la Venta
            Venta venta = new Venta();
            venta.setUsuario(usuario);
            venta.setFechaVenta(LocalDateTime.now());
            venta.setEstado("PAGADO");
            
            // Calculamos el total sumando (precio * cantidad) de cada CarritoDTO
            double total = compra.getItems().stream()
                    .mapToDouble(item -> item.getPrecio() * item.getCantidad())
                    .sum();
            venta.setTotal(total);
            
            Venta nuevaVenta = ventaRepository.save(venta);

            // Convertimos cada CarritoDTO en una entidad DetalleVenta
            List<DetalleVenta> detalles = compra.getItems().stream().map((CarritoDTO item) -> {
                
                // Buscamos el producto real en la DB usando el ID del DTO
                Producto producto = productoRepository.findById(item.getId())
                        .orElseThrow(() -> new RuntimeException("Producto ID " + item.getId() + " no existe"));

                DetalleVenta detalle = new DetalleVenta();
                detalle.setVenta(nuevaVenta);
                detalle.setProducto(producto);
                detalle.setCantidad(item.getCantidad()); // Uso de CarritoDTO
                detalle.setPrecioUnitario(item.getPrecio()); // Uso de CarritoDTO
                detalle.setSubtotal(item.getPrecio() * item.getCantidad());
                
                return detalle;
            }).collect(Collectors.toList());

            // Guardado masivo
            detalleVentaRepository.saveAll(detalles);

            return ResponseEntity.ok(Map.of(
                "idVenta", nuevaVenta.getIdVenta(),
                "mensaje", "Venta realizada con éxito"
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}