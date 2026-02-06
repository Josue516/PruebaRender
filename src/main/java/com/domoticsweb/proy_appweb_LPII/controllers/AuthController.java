package com.domoticsweb.proy_appweb_LPII.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/usuario/panel")
    public String panelUsuario() {
        return "usuario/panel";
    }
}
