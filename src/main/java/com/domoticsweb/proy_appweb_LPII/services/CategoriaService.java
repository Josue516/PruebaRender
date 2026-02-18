package com.domoticsweb.proy_appweb_LPII.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.domoticsweb.proy_appweb_LPII.database.entities.Categoria;
import com.domoticsweb.proy_appweb_LPII.database.repositories.CategoriaRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    // Listar todas
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }
    public List<Categoria> listarActivas(){
        return categoriaRepository.findByActivoTrue();
    }
    // Buscar por ID
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    }

    // Crear categoría
    public Categoria guardar(Categoria categoria) {

        categoriaRepository.findByNombreIgnoreCase(categoria.getNombre())
                .ifPresent(c -> {
                    throw new RuntimeException("La categoría ya existe");
                });

        return categoriaRepository.save(categoria);
    }

    // Eliminación lógica
    public void desactivar(Long id) {

        Categoria categoria = buscarPorId(id);
        categoria.setActivo(!categoria.getActivo());

        categoriaRepository.save(categoria);
    }
}