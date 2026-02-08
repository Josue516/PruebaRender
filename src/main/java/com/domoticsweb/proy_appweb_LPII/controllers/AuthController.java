package com.domoticsweb.proy_appweb_LPII.controllers;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;


import com.domoticsweb.proy_appweb_LPII.database.entities.Usuario;
import com.domoticsweb.proy_appweb_LPII.database.entities.Venta;
import com.domoticsweb.proy_appweb_LPII.database.repositories.UsuarioRepository;
import com.domoticsweb.proy_appweb_LPII.database.repositories.VentaRepository;
import com.domoticsweb.proy_appweb_LPII.dto.UsuarioDTO;

@Controller
public class AuthController {

	private final UsuarioRepository usuarioRepository;
	private final VentaRepository ventaRepository;
	private final PasswordEncoder passwordEncoder;
	
	public AuthController(UsuarioRepository usuarioRepository, VentaRepository ventaRepository, PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.ventaRepository = ventaRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/usuario/panel")
    public String panelUsuario(Authentication authentication, 
                               @RequestParam(name="editar", required=false, defaultValue="false") boolean editar,
                               Model model) {
        
        String username = authentication.getName();
        Usuario user = usuarioRepository.findByNombreUsuarioIgnoreCase(username).orElseThrow();

        List<Venta> ventas = ventaRepository.findByUsuarioOrderByFechaVentaDesc(user);
        int totalPedidos = ventas.size();
        double totalGastado = ventas.stream().mapToDouble(Venta::getTotal).sum();

        // Incializacion DTO para form
        UsuarioDTO usuarioForm = new UsuarioDTO();
        usuarioForm.setNombreUsuario(user.getNombreUsuario());
        usuarioForm.setCorreo(user.getCorreo());

        model.addAttribute("user", user);
        model.addAttribute("usuarioForm", usuarioForm);
        model.addAttribute("ventas", ventas);
        model.addAttribute("totalPedidos", totalPedidos);
        model.addAttribute("totalGastado", totalGastado);
        model.addAttribute("editable", editar);

        return "usuario/panel";
    }

    @PostMapping("/usuario/actualizar")
    public String actualizarPerfil(@ModelAttribute("usuarioForm") UsuarioDTO form,
                                   Authentication authentication,
                                   Model model) {

        // Traer usuario actual desde la base de datos
        String username = authentication.getName();
        Usuario user = usuarioRepository.findByNombreUsuarioIgnoreCase(username).orElseThrow();

        // Actualizar campos editables
        user.setNombreUsuario(form.getNombreUsuario());
        user.setCorreo(form.getCorreo());

        // Actualizar contraseña solo si se ingreso algo
        if (form.getNuevaContrasena() != null && !form.getNuevaContrasena().isBlank()) {
            String hash = passwordEncoder.encode(form.getNuevaContrasena());
            user.setContrasenaHash(hash);
        }

        // Guardar cambios
        usuarioRepository.save(user);
        
        // Nueva autenticacion para los datos del usuario cambiados
        var newAuth = new UsernamePasswordAuthenticationToken(user.getNombreUsuario(), authentication.getCredentials(), authentication.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(newAuth);

        // Se envian todos los datos mas el mensaje de exito
        model.addAttribute("user", user);
        model.addAttribute("usuarioForm", form);
        model.addAttribute("successMessage", "Perfil actualizado correctamente ✅");
        model.addAttribute("editable", false);

        return "usuario/panel";
    }


}
