package com.mycompany.pelucanina.controller;

import com.mycompany.pelucanina.repository.SolicitudTurnoRepository;
import com.mycompany.pelucanina.model.SolicitudTurno;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudTurnoViewController {

    private final SolicitudTurnoRepository solicitudTurnoRepository;

    public SolicitudTurnoViewController(SolicitudTurnoRepository solicitudTurnoRepository) {
        this.solicitudTurnoRepository = solicitudTurnoRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("solicitudes", solicitudTurnoRepository.findAllByOrderByFechaSolicitudDesc());
        return "solicitudes/lista";
    }

    @GetMapping("/confirmar/{id}")
    public String confirmar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        solicitudTurnoRepository.findById(id).ifPresent(s -> {
            s.setEstado("CONFIRMADO");
            solicitudTurnoRepository.save(s);
        });
        redirectAttributes.addFlashAttribute("mensajeExito", "Solicitud confirmada");
        return "redirect:/solicitudes";
    }

    @GetMapping("/cancelar/{id}")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        solicitudTurnoRepository.findById(id).ifPresent(s -> {
            s.setEstado("CANCELADO");
            solicitudTurnoRepository.save(s);
        });
        redirectAttributes.addFlashAttribute("mensajeExito", "Solicitud cancelada");
        return "redirect:/solicitudes";
    }
}