package com.domoticsweb.proy_appweb_LPII.database.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.domoticsweb.proy_appweb_LPII.database.entities.ProductoImagen;

@Repository
public interface ProductoImagenRepository extends JpaRepository<ProductoImagen, Long> {
	List<ProductoImagen> findByProducto_IdProducto(Long idProducto); //Lista imagenes de un producto

    Optional<ProductoImagen> findByProducto_IdProductoAndPrincipalTrue(Long idProducto);  //Obtener imagen principal
}