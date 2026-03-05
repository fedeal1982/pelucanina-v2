package com.mycompany.pelucanina.controller;

import com.mycompany.pelucanina.service.MascotaService;
import com.mycompany.pelucanina.service.TurnoService;
import com.mycompany.pelucanina.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final MascotaService mascotaService;
    private final TurnoService turnoService;
    private final UsuarioService usuarioService;

    public HomeController(MascotaService mascotaService, TurnoService turnoService, UsuarioService usuarioService) {
        this.mascotaService = mascotaService;
        this.turnoService = turnoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Estadísticas
        model.addAttribute("totalMascotas", mascotaService.contarMascotas());
        model.addAttribute("turnosHoy", turnoService.obtenerTurnosHoy().size());
        model.addAttribute("totalUsuarios", usuarioService.contarUsuarios());

        return "index";
    }
}