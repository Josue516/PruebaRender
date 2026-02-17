package com.domoticsweb.proy_appweb_LPII.services;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.domoticsweb.proy_appweb_LPII.database.entities.Producto;
import com.domoticsweb.proy_appweb_LPII.database.entities.ProductoImagen;
import com.domoticsweb.proy_appweb_LPII.database.repositories.ProductoRepository;
import com.domoticsweb.proy_appweb_LPII.dto.CarritoDTO;
import com.domoticsweb.proy_appweb_LPII.dto.VentaDTO;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;
    private final InventarioService inventarioService;
    // Listar productos
    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
    	return productoRepository.findByActivoTrue();
    }

    // Buscar por ID
    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    // Crear producto completo
    public Producto crearProducto(Producto producto, Integer stockInicial, Integer stockMinimo) {

        // Validar categoría existente
        categoriaService.buscarPorId(producto.getCategoria().getIdCategoria());

        // Guardar producto primero
        Producto productoGuardado = productoRepository.save(producto);

        // Crear inventario automáticamente
        inventarioService.crearInventario(
                productoGuardado.getIdProducto(),
                stockInicial,
                stockMinimo
        );

        return productoGuardado;
    }

    // Agregar imágenes
    public Producto agregarImagenes(Long idProducto, Set<ProductoImagen> imagenes) {

        Producto producto = buscarPorId(idProducto);

        imagenes.forEach(img -> img.setProducto(producto));

        producto.getImagenes().addAll(imagenes);

        return productoRepository.save(producto);
    }

    // Actualizar producto
    public Producto actualizarProducto(Producto producto) {
        // Verifica que el producto existe
        Producto productoExistente = buscarPorId(producto.getIdProducto());
        
        // Actualiza solo los campos editables
        productoExistente.setNombre(producto.getNombre());
        productoExistente.setMarca(producto.getMarca());
        productoExistente.setPrecio(producto.getPrecio());
        productoExistente.setCategoria(producto.getCategoria());
        productoExistente.setDescripcion(producto.getDescripcion());
        
        // Guarda y retorna
        return productoRepository.save(productoExistente);
    }

    // Eliminación lógica
    public void desactivarProducto(Long id) {

        Producto producto = buscarPorId(id);
        producto.setActivo(false);

        productoRepository.save(producto);
    }
    // Listar por categoría
    @Transactional(readOnly = true)
    public List<Producto> listarPorCategoria(Long idCategoria) {
    	return productoRepository.findByCategoria_IdCategoriaAndActivoTrue(idCategoria);
    }
    //PARA REDUCIR STOCK DE INVENTARIO
    @Transactional
    public void procesarVenta(VentaDTO venta) {
        try {
            for (CarritoDTO item : venta.getItems()) {
                inventarioService.reducirStock(item.getId(), item.getCantidad());
            }
            System.out.println("Venta procesada exitosamente y stock actualizado");
        } catch (RuntimeException e) {
            System.err.println("Error al procesar venta: " + e.getMessage());
            throw e; // Re-lanzar para que el @Transactional haga rollback
        }
    }
	public void suspender(Long id){
    Producto p = productoRepository.findById(id).orElseThrow();
    p.setActivo(!p.getActivo());
    productoRepository.save(p);
}
	public List<Producto> filtrarProductos(String nombre, Long idCategoria, Boolean activo) {
	    return productoRepository.filtrarProductos(nombre, idCategoria, activo);
	}
}
