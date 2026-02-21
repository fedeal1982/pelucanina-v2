package com.mycompany.pelucanina.controller;

import com.mycompany.pelucanina.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // GET /usuarios
    @GetMapping
    public String listar(Model model, Authentication auth) {
        model.addAttribute("usuarios", usuarioService.obtenerTodosLosUsuarios());
        model.addAttribute("usuarioActual", auth.getName());
        return "usuarios/lista";
    }

    // GET /usuarios/desactivar/{id}
    @GetMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id, Authentication auth,
                             RedirectAttributes redirectAttributes) {
        usuarioService.obtenerPorId(id).ifPresent(usuario -> {
            if (usuario.getUsername().equals(auth.getName())) {
                redirectAttributes.addFlashAttribute("mensajeError",
                    "No podés desactivar tu propio usuario");
                return;
            }
            usuario.setActivo(false);
            usuarioService.guardar(usuario);
            redirectAttributes.addFlashAttribute("mensajeExito",
                "Usuario desactivado correctamente");
        });
        return "redirect:/usuarios";
    }

    // GET /usuarios/activar/{id}
    @GetMapping("/activar/{id}")
    public String activar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        usuarioService.obtenerPorId(id).ifPresent(usuario -> {
            usuario.setActivo(true);
            usuarioService.guardar(usuario);
            redirectAttributes.addFlashAttribute("mensajeExito",
                "Usuario activado correctamente");
        });
        return "redirect:/usuarios";
    }

    // GET /usuarios/cambiar-rol/{id}
    @GetMapping("/cambiar-rol/{id}")
    public String cambiarRol(@PathVariable Long id, Authentication auth,
                             RedirectAttributes redirectAttributes) {
        usuarioService.obtenerPorId(id).ifPresent(usuario -> {
            if (usuario.getUsername().equals(auth.getName())) {
                redirectAttributes.addFlashAttribute("mensajeError",
                    "No podés cambiar tu propio rol");
                return;
            }
            usuario.setRol(usuario.getRol().equals("ADMIN") ? "EMPLEADO" : "ADMIN");
            usuarioService.guardar(usuario);
            redirectAttributes.addFlashAttribute("mensajeExito",
                "Rol actualizado correctamente");
        });
        return "redirect:/usuarios";
    }
}