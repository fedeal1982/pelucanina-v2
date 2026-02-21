package com.mycompany.pelucanina.controller;

import com.mycompany.pelucanina.model.Raza;
import com.mycompany.pelucanina.repository.RazaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/razas")
public class RazaController {

    private final RazaRepository razaRepository;

    public RazaController(RazaRepository razaRepository) {
        this.razaRepository = razaRepository;
    }

    // GET /razas
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("razas", razaRepository.findAll());
        return "razas/lista";
    }

    // GET /razas/nueva
    @GetMapping("/nueva")
    public String formularioNueva(Model model) {
        model.addAttribute("raza", new Raza());
        model.addAttribute("esNuevo", true);
        return "razas/formulario";
    }

    // POST /razas
    @PostMapping
    public String guardar(@ModelAttribute Raza raza, RedirectAttributes redirectAttributes) {
        try {
            razaRepository.save(raza);
            redirectAttributes.addFlashAttribute("mensajeExito", "Raza guardada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/razas";
    }

    // GET /razas/editar/{id}
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Integer id, Model model) {
        return razaRepository.findById(id).map(raza -> {
            model.addAttribute("raza", raza);
            model.addAttribute("esNuevo", false);
            return "razas/formulario";
        }).orElse("redirect:/razas");
    }

    // POST /razas/editar/{id}
    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Integer id, @ModelAttribute Raza raza,
                             RedirectAttributes redirectAttributes) {
        try {
            raza.setIdRaza(id);
            razaRepository.save(raza);
            redirectAttributes.addFlashAttribute("mensajeExito", "Raza actualizada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/razas";
    }

    // GET /razas/eliminar/{id}
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            razaRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Raza eliminada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", 
                "No se puede eliminar: la raza está siendo usada por una o más mascotas");
        }
        return "redirect:/razas";
    }
}