package com.mycompany.pelucanina.controller;

import com.mycompany.pelucanina.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
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

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        if (error != null) model.addAttribute("error", "Usuario o contraseña incorrectos");
        if (logout != null) model.addAttribute("mensaje", "Sesión cerrada correctamente");
        return "auth/login";
    }

    @GetMapping("/register")
    public String mostrarRegistro() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registrar(@RequestParam String username,
                            @RequestParam String password,
                            @RequestParam String nombreCompleto,
                            @RequestParam(defaultValue = "EMPLEADO") String rol,
                            @RequestParam(required = false) String email,
                            RedirectAttributes redirectAttributes) {
        try {
            usuarioService.registrarUsuario(username, password, nombreCompleto, rol, email);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario registrado correctamente.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/cambiar-password")
    public String mostrarCambiarPassword() {
        return "auth/cambiar-password";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam String passwordActual,
                                  @RequestParam String passwordNueva,
                                  @RequestParam String passwordConfirm,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (!passwordNueva.equals(passwordConfirm)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas nuevas no coinciden");
                return "redirect:/cambiar-password";
            }
            usuarioService.cambiarPassword(authentication.getName(), passwordActual, passwordNueva);
            redirectAttributes.addFlashAttribute("mensaje", "Contraseña cambiada correctamente");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cambiar-password";
        }
    }

    @GetMapping("/recuperar-password")
    public String mostrarRecuperar() {
        return "auth/recuperar-password";
    }

    @PostMapping("/recuperar-password")
    public String recuperarPassword(@RequestParam String email,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes) {
        try {
            String baseUrl = request.getScheme() + "://" + request.getServerName()
                + (request.getServerPort() != 80 && request.getServerPort() != 443 ? ":" + request.getServerPort() : "");
            usuarioService.enviarEmailRecuperacion(email, baseUrl);
            redirectAttributes.addFlashAttribute("mensaje", "Se envió un link de recuperación a tu email");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/recuperar-password";
        }
    }

    @GetMapping("/reset-password")
    public String mostrarReset(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String passwordNueva,
                                @RequestParam String passwordConfirm,
                                RedirectAttributes redirectAttributes) {
        try {
            if (!passwordNueva.equals(passwordConfirm)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
                return "redirect:/reset-password?token=" + token;
            }
            usuarioService.resetearPassword(token, passwordNueva);
            redirectAttributes.addFlashAttribute("mensaje", "Contraseña restablecida correctamente.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }
}