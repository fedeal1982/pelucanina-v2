package com.mycompany.pelucanina.controller;

import com.mycompany.pelucanina.model.Vacuna;
import com.mycompany.pelucanina.service.MascotaService;
import com.mycompany.pelucanina.service.UsuarioService;
import com.mycompany.pelucanina.service.VacunaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/vacunas")
public class VacunaController {

    private final VacunaService vacunaService;
    private final MascotaService mascotaService;
    private final UsuarioService usuarioService;

    public VacunaController(VacunaService vacunaService, MascotaService mascotaService, UsuarioService usuarioService) {
        this.vacunaService = vacunaService;
        this.mascotaService = mascotaService;
        this.usuarioService = usuarioService;
    }

    // GET /vacunas/mascota/{id}
    @GetMapping("/mascota/{id}")
    public String verVacunas(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return mascotaService.obtenerMascotaPorId(id).map(mascota -> {
            model.addAttribute("mascota", mascota);
            model.addAttribute("vacunas", vacunaService.obtenerPorMascota(id));
            return "vacunas/lista";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("mensajeError", "Mascota no encontrada");
            return "redirect:/mascotas";
        });
    }

    // GET /vacunas/nuevo/{mascotaId}
    @GetMapping("/nuevo/{mascotaId}")
    public String nuevo(@PathVariable Integer mascotaId, Model model, RedirectAttributes redirectAttributes) {
        return mascotaService.obtenerMascotaPorId(mascotaId).map(mascota -> {
            model.addAttribute("mascota", mascota);
            model.addAttribute("vacuna", new Vacuna());
            model.addAttribute("veterinarios", usuarioService.obtenerTodosLosUsuarios());
            model.addAttribute("esNuevo", true);
            return "vacunas/formulario";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("mensajeError", "Mascota no encontrada");
            return "redirect:/mascotas";
        });
    }

    // POST /vacunas/nuevo/{mascotaId}
    @PostMapping("/nuevo/{mascotaId}")
    public String guardar(@PathVariable Integer mascotaId,
                          @RequestParam String nombre,
                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaAplicacion,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate proximaDosis,
                          @RequestParam(required = false) Long veterinarioId,
                          @RequestParam(required = false) String observaciones,
                          RedirectAttributes redirectAttributes) {
        try {
            Vacuna vacuna = new Vacuna();
            vacuna.setNombre(nombre);
            vacuna.setFechaAplicacion(fechaAplicacion);
            vacuna.setProximaDosis(proximaDosis);
            vacuna.setObservaciones(observaciones);
            mascotaService.obtenerMascotaPorId(mascotaId).ifPresent(vacuna::setMascota);
            if (veterinarioId != null) {
                usuarioService.obtenerPorId(veterinarioId).ifPresent(vacuna::setVeterinario);
            }
            vacunaService.guardar(vacuna);
            redirectAttributes.addFlashAttribute("mensajeExito", "Vacuna registrada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/vacunas/mascota/" + mascotaId;
    }

    // GET /vacunas/editar/{id}
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return vacunaService.obtenerPorId(id).map(vacuna -> {
            model.addAttribute("vacuna", vacuna);
            model.addAttribute("mascota", vacuna.getMascota());
            model.addAttribute("veterinarios", usuarioService.obtenerTodosLosUsuarios());
            model.addAttribute("esNuevo", false);
            return "vacunas/formulario";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("mensajeError", "Vacuna no encontrada");
            return "redirect:/mascotas";
        });
    }

    // POST /vacunas/editar/{id}
    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @RequestParam String nombre,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaAplicacion,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate proximaDosis,
                             @RequestParam(required = false) Long veterinarioId,
                             @RequestParam(required = false) String observaciones,
                             RedirectAttributes redirectAttributes) {
        vacunaService.obtenerPorId(id).ifPresent(vacuna -> {
            vacuna.setNombre(nombre);
            vacuna.setFechaAplicacion(fechaAplicacion);
            vacuna.setProximaDosis(proximaDosis);
            vacuna.setObservaciones(observaciones);
            if (veterinarioId != null) {
                usuarioService.obtenerPorId(veterinarioId).ifPresent(vacuna::setVeterinario);
            } else {
                vacuna.setVeterinario(null);
            }
            vacunaService.guardar(vacuna);
        });
        redirectAttributes.addFlashAttribute("mensajeExito", "Vacuna actualizada correctamente");
        Integer mascotaId = vacunaService.obtenerPorId(id)
                .map(v -> v.getMascota().getNumCliente()).orElse(0);
        return "redirect:/vacunas/mascota/" + mascotaId;
    }

    // GET /vacunas/eliminar/{id}
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Integer mascotaId = vacunaService.obtenerPorId(id)
                .map(v -> v.getMascota().getNumCliente()).orElse(0);
        vacunaService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Vacuna eliminada correctamente");
        return "redirect:/vacunas/mascota/" + mascotaId;
    }
}