package com.mycompany.pelucanina.controller;

import com.mycompany.pelucanina.service.AuditoriaService;
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
    private final AuditoriaService auditoriaService;

    public UsuarioController(UsuarioService usuarioService, AuditoriaService auditoriaService) {
        this.usuarioService = usuarioService;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public String listar(Model model, Authentication auth) {
        model.addAttribute("usuarios", usuarioService.obtenerTodosLosUsuarios());
        model.addAttribute("usuarioActual", auth.getName());
        return "usuarios/lista";
    }

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
            auditoriaService.registrar(auth.getName(), "USUARIO", "Usuario",
                "Desactivó usuario: " + usuario.getUsername());
            redirectAttributes.addFlashAttribute("mensajeExito",
                "Usuario desactivado correctamente");
        });
        return "redirect:/usuarios";
    }

    @GetMapping("/activar/{id}")
    public String activar(@PathVariable Long id, Authentication auth,
                          RedirectAttributes redirectAttributes) {
        usuarioService.obtenerPorId(id).ifPresent(usuario -> {
            usuario.setActivo(true);
            usuarioService.guardar(usuario);
            auditoriaService.registrar(auth.getName(), "USUARIO", "Usuario",
                "Activó usuario: " + usuario.getUsername());
            redirectAttributes.addFlashAttribute("mensajeExito",
                "Usuario activado correctamente");
        });
        return "redirect:/usuarios";
    }

    @GetMapping("/cambiar-rol/{id}")
    public String cambiarRol(@PathVariable Long id,
                             @RequestParam String nuevoRol,
                             Authentication auth,
                             RedirectAttributes redirectAttributes) {
        usuarioService.obtenerPorId(id).ifPresent(usuario -> {
            if (usuario.getUsername().equals(auth.getName())) {
                redirectAttributes.addFlashAttribute("mensajeError",
                    "No podés cambiar tu propio rol");
                return;
            }
            usuario.setRol(nuevoRol);
            usuarioService.guardar(usuario);
            auditoriaService.registrar(auth.getName(), "USUARIO", "Usuario",
                "Cambió rol de " + usuario.getUsername() + " a " + nuevoRol);
            redirectAttributes.addFlashAttribute("mensajeExito",
                "Rol actualizado a " + nuevoRol);
        });
        return "redirect:/usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Authentication auth,
                           RedirectAttributes redirectAttributes) {
        usuarioService.obtenerPorId(id).ifPresent(usuario -> {
            if (usuario.getUsername().equals(auth.getName())) {
                redirectAttributes.addFlashAttribute("mensajeError",
                    "No podés eliminar tu propio usuario");
                return;
            }
            auditoriaService.registrar(auth.getName(), "ELIMINACION", "Usuario",
                "Eliminó usuario: " + usuario.getUsername());
            usuarioService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito",
                "Usuario eliminado correctamente");
        });
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String mostrarEditar(@PathVariable Long id, Model model,
                                RedirectAttributes redirectAttributes) {
        return usuarioService.obtenerPorId(id).map(usuario -> {
            model.addAttribute("usuario", usuario);
            return "usuarios/formulario";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("mensajeError", "Usuario no encontrado");
            return "redirect:/usuarios";
        });
    }

    @PostMapping("/editar/{id}")
    public String guardarEdicion(@PathVariable Long id,
                                 @RequestParam String nombreCompleto,
                                 @RequestParam String rol,
                                 @RequestParam(required = false) String email,
                                 Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        usuarioService.obtenerPorId(id).ifPresent(usuario -> {
            usuario.setNombreCompleto(nombreCompleto);
            usuario.setRol(rol);
            usuario.setEmail(email);
            usuarioService.guardar(usuario);
            auditoriaService.registrar(auth.getName(), "USUARIO", "Usuario",
                "Editó usuario: " + usuario.getUsername());
            redirectAttributes.addFlashAttribute("mensajeExito", "Usuario actualizado correctamente");
        });
        return "redirect:/usuarios";
    }
}