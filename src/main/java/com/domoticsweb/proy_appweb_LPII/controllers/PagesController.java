package com.domoticsweb.proy_appweb_LPII.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.domoticsweb.proy_appweb_LPII.database.entities.Producto;
import com.domoticsweb.proy_appweb_LPII.database.repositories.CategoriaRepository;
import com.domoticsweb.proy_appweb_LPII.database.repositories.ProductoRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PagesController {

	private final ProductoRepository productoRepository;
	private final CategoriaRepository categoriaRepository;
    @GetMapping("/")
	public String home() {
		return "pages/home";
	}
	
	
	@GetMapping("/nosotros")
	public String about() {
		return "pages/nosotros";
	}
	
	@GetMapping("/contacto")
	public String contact() {
		return "pages/contacto";
	}
	
	@GetMapping("/productos")
    public String productos(@RequestParam(name = "idCat", required = false) Long idCat, 
                            Model model, 
                            HttpServletRequest request) {
        
        List<Producto> productos = (idCat != null) 
            ? productoRepository.findByCategoria_IdCategoria(idCat) 
            : productoRepository.findAll();

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("categoriaSeleccionada", idCat != null ? idCat : "todos");

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "pages/productos :: productosGrid";
        }

        return "pages/productos";
    }
}
