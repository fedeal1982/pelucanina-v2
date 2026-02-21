package com.mycompany.pelucanina.controller;

import com.mycompany.pelucanina.service.MascotaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para la página principal
 * 
 * @Controller = Esta clase maneja peticiones web
 * @GetMapping = Responde a peticiones GET (cuando visitas una URL)
 */
@Controller
public class HomeController {

    private final MascotaService mascotaService;

    public HomeController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    /**
     * Página principal (http://localhost:8080/)
     * 
     * Model = Datos que se envían a la página HTML
     * return "index" = Busca el archivo templates/index.html
     */
    @GetMapping("/")
    public String home(Model model) {
        long totalMascotas = mascotaService.contarMascotas();
        long totalEliminadas = mascotaService.contarMascotasEliminadas();
        
        model.addAttribute("totalMascotas", totalMascotas);
        model.addAttribute("totalEliminadas", totalEliminadas);
        
        return "index";
    }
}