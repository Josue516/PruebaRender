package com.domoticsweb.proy_appweb_LPII.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.domoticsweb.proy_appweb_LPII.database.entities.Inventario;
import com.domoticsweb.proy_appweb_LPII.database.entities.Producto;
import com.domoticsweb.proy_appweb_LPII.database.repositories.InventarioRepository;
import com.domoticsweb.proy_appweb_LPII.database.repositories.ProductoRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;

    // Listar todo el inventario
    @Transactional(readOnly = true)
    public List<Inventario> listarTodo() {
        return inventarioRepository.findAll();
    }

    // Buscar inventario por ID
    @Transactional(readOnly = true)
    public Inventario buscarPorId(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));
    }

    // Buscar inventario por producto
    @Transactional(readOnly = true)
    public Inventario buscarPorProducto(Long idProducto) {
        return inventarioRepository.findByProducto_IdProducto(idProducto)
                .orElseThrow(() -> new RuntimeException("Inventario no existe para ese producto"));
    }

    // Crear inventario para un producto
    public Inventario crearInventario(Long idProducto, Integer stockInicial, Integer stockMinimo) {

        if (stockInicial < 0 || stockMinimo < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Evitar duplicar inventario para el mismo producto
        inventarioRepository.findByProducto_IdProducto(idProducto)
                .ifPresent(i -> {
                    throw new RuntimeException("Ese producto ya tiene inventario registrado");
                });

        Inventario inventario = Inventario.builder()
                .producto(producto)
                .stock(stockInicial)
                .stockMinimo(stockMinimo)
                .build();

        return inventarioRepository.save(inventario);
    }

    // Aumentar stock
    public Inventario aumentarStock(Long idProducto, Integer cantidad) {

        if (cantidad <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a cero");
        }

        Inventario inventario = buscarPorProducto(idProducto);

        inventario.setStock(inventario.getStock() + cantidad);

        return inventarioRepository.save(inventario);
    }

    // Reducir stock
    public Inventario reducirStock(Long idProducto, Integer cantidad) {

        if (cantidad <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a cero");
        }

        Inventario inventario = buscarPorProducto(idProducto);

        if (inventario.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente");
        }

        inventario.setStock(inventario.getStock() - cantidad);

        return inventarioRepository.save(inventario);
    }

    // Actualizar stock mínimo
    public Inventario actualizarStockMinimo(Long idProducto, Integer nuevoStockMinimo) {

        if (nuevoStockMinimo < 0) {
            throw new RuntimeException("Stock mínimo no puede ser negativo");
        }

        Inventario inventario = buscarPorProducto(idProducto);

        inventario.setStockMinimo(nuevoStockMinimo);

        return inventarioRepository.save(inventario);
    }

}
