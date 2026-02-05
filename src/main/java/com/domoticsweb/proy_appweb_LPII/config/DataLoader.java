package com.domoticsweb.proy_appweb_LPII.config;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.domoticsweb.proy_appweb_LPII.database.entities.Categoria;
import com.domoticsweb.proy_appweb_LPII.database.entities.Producto;
import com.domoticsweb.proy_appweb_LPII.database.entities.ProductoImagen;
import com.domoticsweb.proy_appweb_LPII.database.repositories.CategoriaRepository;
import com.domoticsweb.proy_appweb_LPII.database.repositories.ProductoRepository;
import com.domoticsweb.proy_appweb_LPII.services.CategoriaService;
import com.domoticsweb.proy_appweb_LPII.services.ProductoService;

import aj.org.objectweb.asm.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CategoriaService categoriaService;
    private final CategoriaRepository categoriaRepository;
    private final ProductoService productoService;
    private final ProductoRepository productoRepository;
    private final ObjectMapper objectMapper; // Spring ya lo tiene configurado

    @Override
    public void run(String... args) throws Exception {
        // Leer el archivo desde resources
        InputStream inputStream = TypeReference.class.getResourceAsStream("/productos.json");
        WrapperData data = objectMapper.readValue(inputStream, WrapperData.class);

        // Cargar Categorías primero
        for (CategoryDTO catDto : data.getCategories()) { // <-- Aquí estaba el error
            // Ahora accedemos al nombre a través del DTO: catDto.getName()
            if (categoriaRepository.findByNombre(catDto.getName()).isEmpty()) {
                categoriaService.guardar(Categoria.builder()
                        .nombre(catDto.getName())
                        .descripcion(catDto.getDescription())
                        .activo(true)
                        .build());
            }
        }

        // Cargar Productos
        for (ProductDTO dto : data.getProducts()) {
            if (!productoRepository.existsByNombre(dto.getTitle())) {
                Categoria cat = categoriaRepository.findByNombre(dto.getCategory()).orElseThrow();

                Producto p = Producto.builder()
                        .nombre(dto.getTitle())        
                        .marca(dto.getBrand())         
                        .descripcion(dto.getDescription())
                        .precio(BigDecimal.valueOf(dto.getPrice()))
                        .categoria(cat)
                        .activo(true)
                        .build();

                // Vincular la imagen
                ProductoImagen img = ProductoImagen.builder()
                        .urlImagen(dto.getImage())
                        .principal(true)
                        .producto(p)
                        .build();
                
                p.setImagenes(List.of(img));

                productoService.crearProducto(p, 15, 3);
            }
        }
        System.out.println(">>> ¡Base de datos sincronizada con productos.json!");

    }
    @Data
    public static class WrapperData {
        private List<ProductDTO> products;
        private List<CategoryDTO> categories;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDTO {
        private String name;
        private String description;
    }
    
    @Data
    public static class ProductDTO {
        private String title;
        private String description;
        private String brand;
        private Double price;
        private String category;
        private String image;
    }
}