package com.domoticsweb.proy_appweb_LPII.services;

import java.util.List;

import com.domoticsweb.proy_appweb_LPII.database.entities.EstadoVenta;
import org.springframework.stereotype.Service;

import com.domoticsweb.proy_appweb_LPII.database.entities.DetalleVenta;
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


    // Crear inventario para un producto
    public Inventario crearInventario(Long idProducto, Integer stockInicial, Integer stockMinimo) {

        if (stockInicial < 0 || stockMinimo < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Evitar duplicar inventario para el mismo producto
        inventarioRepository.findByProductoId(idProducto)
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
    @Transactional
    public void reducirStock(Long idProducto, Integer cantidad) {
        // Buscar el producto
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + idProducto));
        
        // Obtener el inventario del producto
        Inventario inventario = producto.getInventario();
        
        if (inventario == null) {
            throw new RuntimeException("El producto no tiene inventario asociado");
        }
        
        // Verificar que hay stock suficiente
        if (inventario.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + 
                                     inventario.getStock() + ", Solicitado: " + cantidad);
        }
        
        // Reducir el stock
        int nuevoStock = inventario.getStock() - cantidad;
        inventario.setStock(nuevoStock);
        inventarioRepository.save(inventario);
        
        // SI EL STOCK LLEGA A 0, SUSPENDER EL PRODUCTO
        if (nuevoStock == 0) {
            producto.setActivo(false);
            productoRepository.save(producto);
        }
    }
    //ESTE METODO PODRA SER USADO EN CASO DE DEVOLUCIONES
    @Transactional
    public void incrementarStock(Long idProducto, Integer cantidad) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + idProducto));
        
        Inventario inventario = producto.getInventario();
        
        if (inventario == null) {
            throw new RuntimeException("El producto no tiene inventario asociado");
        }
        
        inventario.setStock(inventario.getStock() + cantidad);
        inventarioRepository.save(inventario);
    }
    public List<Inventario> listarTodos() {
        return inventarioRepository.findAll();
    }

    public List<Inventario> filtrarInventario(String nombre, Long idCategoria, EstadoVenta estado) {
        return inventarioRepository.filtrarInventario(nombre, idCategoria, estado);
    }

    public void actualizarStock(Long idInventario, Integer stock, Integer stockMinimo) {
        Inventario inventario = inventarioRepository.findById(idInventario)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));
        
        inventario.setStock(stock);
        inventario.setStockMinimo(stockMinimo);
        inventarioRepository.save(inventario);

    }
    @Transactional
    public void restaurarStockVenta(List<DetalleVenta> detalles) {
        for (DetalleVenta detalle : detalles) {
            incrementarStock(detalle.getProducto().getIdProducto(), detalle.getCantidad());
        }
    }
}
