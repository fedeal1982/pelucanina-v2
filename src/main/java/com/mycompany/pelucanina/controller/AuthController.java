package com.mycompany.pelucanina.controller;

import com.mycompany.pelucanina.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Mostrar página de login
     * GET /login
     */
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                       @RequestParam(required = false) String logout,
                       Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        
        if (logout != null) {
            model.addAttribute("mensaje", "Sesión cerrada correctamente");
        }
        
        return "auth/login";
    }

    /**
     * Mostrar página de registro
     * GET /register
     */
    @GetMapping("/register")
    public String mostrarRegistro() {
        return "auth/register";
    }

    /**
     * Procesar registro de nuevo usuario
     * POST /register
     */
    @PostMapping("/register")
    public String registrar(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String nombreCompleto,
                           @RequestParam(defaultValue = "EMPLEADO") String rol,
                           RedirectAttributes redirectAttributes) {
        try {
            if (usuarioService.existeUsername(username)) {
                redirectAttributes.addFlashAttribute("error", 
                    "El username ya existe. Por favor elija otro.");
                return "redirect:/register";
            }
            
            usuarioService.registrarUsuario(username, password, nombreCompleto, rol);
            
            redirectAttributes.addFlashAttribute("mensaje", 
                "Usuario registrado correctamente. Ya puede iniciar sesión.");
            
            return "redirect:/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al registrar: " + e.getMessage());
            return "redirect:/register";
        }
    }
}