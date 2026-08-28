package com.mycompany.pelucanina.controller;

import com.mycompany.pelucanina.service.MascotaService;
import com.mycompany.pelucanina.service.VacunaService;
import com.mycompany.pelucanina.service.TurnoService;
import com.mycompany.pelucanina.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final MascotaService mascotaService;
    private final TurnoService turnoService;
    private final UsuarioService usuarioService;
    private final VacunaService vacunaService;

    public HomeController(MascotaService mascotaService, TurnoService turnoService,
            UsuarioService usuarioService, VacunaService vacunaService) {
        this.mascotaService = mascotaService;
        this.turnoService = turnoService;
        this.usuarioService = usuarioService;
        this.vacunaService = vacunaService;
    }

    // Sistema interno (requiere login)
    @GetMapping("/")
    public String redirectInicio() {
        return "redirect:/inicio";
    }

    @GetMapping("/admin")
    public String home(Model model) {
        model.addAttribute("totalMascotas", mascotaService.contarMascotas());
        model.addAttribute("turnosHoy", turnoService.obtenerTurnosHoy().size());
        model.addAttribute("totalUsuarios", usuarioService.contarUsuarios());
        model.addAttribute("vacunasVencidas", vacunaService.contarVacunasVencidas());
        return "index";
    }

}