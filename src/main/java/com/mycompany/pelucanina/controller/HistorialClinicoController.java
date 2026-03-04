package com.mycompany.pelucanina.controller;

import com.mycompany.pelucanina.model.HistorialClinico;
import com.mycompany.pelucanina.service.HistorialClinicoService;
import com.mycompany.pelucanina.service.MascotaService;
import com.mycompany.pelucanina.service.UsuarioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/historial")
public class HistorialClinicoController {

    private final HistorialClinicoService historialService;
    private final MascotaService mascotaService;
    private final UsuarioService usuarioService;

    public HistorialClinicoController(HistorialClinicoService historialService,
                                      MascotaService mascotaService,
                                      UsuarioService usuarioService) {
        this.historialService = historialService;
        this.mascotaService = mascotaService;
        this.usuarioService = usuarioService;
    }

    // GET /historial/mascota/{id} - ver historial de una mascota
    @GetMapping("/mascota/{id}")
    public String verHistorial(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return mascotaService.obtenerMascotaPorId(id).map(mascota -> {
            model.addAttribute("mascota", mascota);
            model.addAttribute("historial", historialService.obtenerPorMascota(id));
            return "historial/lista";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("mensajeError", "Mascota no encontrada");
            return "redirect:/mascotas";
        });
    }

    // GET /historial/nuevo/{mascotaId}
    @GetMapping("/nuevo/{mascotaId}")
    public String nuevo(@PathVariable Integer mascotaId, Model model, RedirectAttributes redirectAttributes) {
        return mascotaService.obtenerMascotaPorId(mascotaId).map(mascota -> {
            model.addAttribute("mascota", mascota);
            model.addAttribute("historial", new HistorialClinico());
            model.addAttribute("veterinarios", usuarioService.obtenerTodosLosUsuarios());
            model.addAttribute("esNuevo", true);
            return "historial/formulario";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("mensajeError", "Mascota no encontrada");
            return "redirect:/mascotas";
        });
    }

    // POST /historial/nuevo/{mascotaId}
    @PostMapping("/nuevo/{mascotaId}")
    public String guardar(@PathVariable Integer mascotaId,
                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaConsulta,
                          @RequestParam(required = false) String motivoConsulta,
                          @RequestParam(required = false) String diagnostico,
                          @RequestParam(required = false) String tratamiento,
                          @RequestParam(required = false) String medicamentos,
                          @RequestParam(required = false) BigDecimal peso,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate proximoControl,
                          @RequestParam(required = false) Long veterinarioId,
                          RedirectAttributes redirectAttributes) {
        try {
            HistorialClinico h = new HistorialClinico();
            h.setFechaConsulta(fechaConsulta);
            h.setMotivoConsulta(motivoConsulta);
            h.setDiagnostico(diagnostico);
            h.setTratamiento(tratamiento);
            h.setMedicamentos(medicamentos);
            h.setPeso(peso);
            h.setProximoControl(proximoControl);
            mascotaService.obtenerMascotaPorId(mascotaId).ifPresent(h::setMascota);
            if (veterinarioId != null) {
                usuarioService.obtenerPorId(veterinarioId).ifPresent(h::setVeterinario);
            }
            historialService.guardar(h);
            redirectAttributes.addFlashAttribute("mensajeExito", "Consulta registrada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/historial/mascota/" + mascotaId;
    }

    // GET /historial/editar/{id}
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return historialService.obtenerPorId(id).map(h -> {
            model.addAttribute("historial", h);
            model.addAttribute("mascota", h.getMascota());
            model.addAttribute("veterinarios", usuarioService.obtenerTodosLosUsuarios());
            model.addAttribute("esNuevo", false);
            return "historial/formulario";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("mensajeError", "Consulta no encontrada");
            return "redirect:/mascotas";
        });
    }

    // POST /historial/editar/{id}
    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaConsulta,
                             @RequestParam(required = false) String motivoConsulta,
                             @RequestParam(required = false) String diagnostico,
                             @RequestParam(required = false) String tratamiento,
                             @RequestParam(required = false) String medicamentos,
                             @RequestParam(required = false) BigDecimal peso,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate proximoControl,
                             @RequestParam(required = false) Long veterinarioId,
                             RedirectAttributes redirectAttributes) {
        historialService.obtenerPorId(id).ifPresent(h -> {
            h.setFechaConsulta(fechaConsulta);
            h.setMotivoConsulta(motivoConsulta);
            h.setDiagnostico(diagnostico);
            h.setTratamiento(tratamiento);
            h.setMedicamentos(medicamentos);
            h.setPeso(peso);
            h.setProximoControl(proximoControl);
            if (veterinarioId != null) {
                usuarioService.obtenerPorId(veterinarioId).ifPresent(h::setVeterinario);
            } else {
                h.setVeterinario(null);
            }
            historialService.guardar(h);
        });
        redirectAttributes.addFlashAttribute("mensajeExito", "Consulta actualizada correctamente");
        Long mascotaId = historialService.obtenerPorId(id)
                .map(h -> h.getMascota().getNumCliente().longValue()).orElse(0L);
        return "redirect:/historial/mascota/" + mascotaId;
    }

    // GET /historial/eliminar/{id}
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        historialService.obtenerPorId(id).ifPresent(h -> {
            Integer mascotaId = h.getMascota().getNumCliente();
            historialService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Consulta eliminada correctamente");
        });
        return "redirect:/mascotas";
    }
}