package com.mycompany.pelucanina.controller;

import com.mycompany.pelucanina.model.Servicio;
import com.mycompany.pelucanina.service.ServicioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/servicios")
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    // GET /servicios
    @GetMapping
    public String lista(Model model) {
        model.addAttribute("servicios", servicioService.obtenerTodos());
        return "servicios/lista";
    }

    // GET /servicios/nuevo
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("servicio", new Servicio());
        model.addAttribute("esNuevo", true);
        return "servicios/formulario";
    }

    // POST /servicios/nuevo
    @PostMapping("/nuevo")
    public String guardar(@RequestParam String nombre,
                          @RequestParam(required = false) String descripcion,
                          @RequestParam(defaultValue = "true") boolean activo,
                          RedirectAttributes redirectAttributes) {
        try {
            Servicio servicio = new Servicio();
            servicio.setNombre(nombre);
            servicio.setDescripcion(descripcion);
            servicio.setActivo(activo);
            servicioService.guardar(servicio);
            redirectAttributes.addFlashAttribute("mensajeExito", "Servicio creado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al crear: " + e.getMessage());
        }
        return "redirect:/servicios";
    }

    // GET /servicios/editar/{id}
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return servicioService.obtenerPorId(id).map(servicio -> {
            model.addAttribute("servicio", servicio);
            model.addAttribute("esNuevo", false);
            return "servicios/formulario";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("mensajeError", "Servicio no encontrado");
            return "redirect:/servicios";
        });
    }

    // POST /servicios/editar/{id}
    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @RequestParam String nombre,
                             @RequestParam(required = false) String descripcion,
                             @RequestParam(defaultValue = "false") boolean activo,
                             RedirectAttributes redirectAttributes) {
        servicioService.obtenerPorId(id).ifPresent(servicio -> {
            servicio.setNombre(nombre);
            servicio.setDescripcion(descripcion);
            servicio.setActivo(activo);
            servicioService.guardar(servicio);
        });
        redirectAttributes.addFlashAttribute("mensajeExito", "Servicio actualizado correctamente");
        return "redirect:/servicios";
    }

    // GET /servicios/toggle/{id}
    @GetMapping("/toggle/{id}")
    public String toggleActivo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        servicioService.obtenerPorId(id).ifPresent(servicio -> {
            servicio.setActivo(!servicio.isActivo());
            servicioService.guardar(servicio);
        });
        redirectAttributes.addFlashAttribute("mensajeExito", "Estado actualizado");
        return "redirect:/servicios";
    }

    // GET /servicios/eliminar/{id}
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        servicioService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Servicio eliminado correctamente");
        return "redirect:/servicios";
    }
}