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
    public Producto actualizarProducto(Long id, Producto productoActualizado) {

        Producto producto = buscarPorId(id);

        producto.setNombre(productoActualizado.getNombre());
        producto.setDescripcion(productoActualizado.getDescripcion());
        producto.setPrecio(productoActualizado.getPrecio());
        producto.setCategoria(productoActualizado.getCategoria());

        return productoRepository.save(producto);
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

    // Buscar por nombre
    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }
    //PARA REDUCIR STOCK DE INVENTARIO
    @Transactional
    public void procesarVenta(VentaDTO venta) {
        for (CarritoDTO item : venta.getItems()) {
            inventarioService.reducirStock(item.getId(), item.getCantidad());
        }
        System.out.println("Venta procesada y stock actualizado mediante InventarioService");
    }
}
