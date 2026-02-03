package com.domoticsweb.proy_appweb_LPII.controllers;

import com.domoticsweb.proy_appweb_LPII.dto.RegistroRequest;
import com.domoticsweb.proy_appweb_LPII.services.RegistroService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistroController {

    private final RegistroService registroService;

    public RegistroController(RegistroService registroService) {
        this.registroService = registroService;
    }

    @GetMapping("/registro")
    public String verRegistro(Model model) {
        model.addAttribute("form", new RegistroRequest());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute("form") RegistroRequest form, Model model) {
        try {
            registroService.registrarUsuario(form);
            return "redirect:/login?registrado=true";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "auth/registro";
        }
    }
}
