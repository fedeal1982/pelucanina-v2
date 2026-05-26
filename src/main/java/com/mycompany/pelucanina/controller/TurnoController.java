package com.mycompany.pelucanina.controller;

import com.mycompany.pelucanina.model.Turno;
import com.mycompany.pelucanina.service.AuditoriaService;
import com.mycompany.pelucanina.service.MascotaService;
import com.mycompany.pelucanina.service.TurnoService;
import com.mycompany.pelucanina.service.UsuarioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/turnos")
public class TurnoController {

    private final TurnoService turnoService;
    private final MascotaService mascotaService;
    private final UsuarioService usuarioService;
    private final AuditoriaService auditoriaService;

    public TurnoController(TurnoService turnoService, MascotaService mascotaService,
                           UsuarioService usuarioService, AuditoriaService auditoriaService) {
        this.turnoService = turnoService;
        this.mascotaService = mascotaService;
        this.usuarioService = usuarioService;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public String lista(@RequestParam(required = false) String estado, Model model) {
        if (estado != null && !estado.isEmpty()) {
            model.addAttribute("turnos", turnoService.obtenerPorEstado(estado));
            model.addAttribute("estadoFiltro", estado);
        } else {
            model.addAttribute("turnos", turnoService.obtenerTodos());
            model.addAttribute("estadoFiltro", "");
        }
        return "turnos/lista";
    }

    @GetMapping("/calendario")
    public String calendario(Model model) {
        model.addAttribute("turnos", turnoService.obtenerProximos7Dias());
        return "turnos/calendario";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("mascotas", mascotaService.obtenerTodasLasMascotas());
        model.addAttribute("empleados", usuarioService.obtenerTodosLosUsuarios());
        return "turnos/formulario";
    }

    @PostMapping("/nuevo")
    public String guardar(@RequestParam Integer mascotaId,
                          @RequestParam(required = false) Long empleadoId,
                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fechaHora,
                          @RequestParam(required = false) String servicio,
                          @RequestParam(required = false) BigDecimal precio,
                          @RequestParam(defaultValue = "PENDIENTE") String estado,
                          @RequestParam(required = false) String observaciones,
                          RedirectAttributes redirectAttributes,
                          Authentication authentication) {
        try {
            Turno turno = new Turno();
            turno.setFechaHora(fechaHora);
            turno.setServicio(servicio);
            turno.setPrecio(precio);
            turno.setEstado(estado);
            turno.setObservaciones(observaciones);

            mascotaService.obtenerMascotaPorId(mascotaId).ifPresent(turno::setMascota);
            if (empleadoId != null) {
                usuarioService.obtenerPorId(empleadoId).ifPresent(turno::setEmpleado);
            }

            turnoService.guardar(turno);
            auditoriaService.registrar(authentication.getName(), "TURNO", "Turno",
                "Nuevo turno para mascota ID: " + mascotaId + (servicio != null ? " - " + servicio : ""));
            redirectAttributes.addFlashAttribute("mensajeExito", "Turno creado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al crear el turno: " + e.getMessage());
        }
        return "redirect:/turnos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return turnoService.obtenerPorId(id).map(turno -> {
            model.addAttribute("turno", turno);
            model.addAttribute("mascotas", mascotaService.obtenerTodasLasMascotas());
            model.addAttribute("empleados", usuarioService.obtenerTodosLosUsuarios());
            return "turnos/formulario";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("mensajeError", "Turno no encontrado");
            return "redirect:/turnos";
        });
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @RequestParam Integer mascotaId,
                             @RequestParam(required = false) Long empleadoId,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fechaHora,
                             @RequestParam(required = false) String servicio,
                             @RequestParam(required = false) BigDecimal precio,
                             @RequestParam(defaultValue = "PENDIENTE") String estado,
                             @RequestParam(required = false) String observaciones,
                             RedirectAttributes redirectAttributes,
                             Authentication authentication) {
        turnoService.obtenerPorId(id).ifPresent(turno -> {
            turno.setFechaHora(fechaHora);
            turno.setServicio(servicio);
            turno.setPrecio(precio);
            turno.setEstado(estado);
            turno.setObservaciones(observaciones);
            mascotaService.obtenerMascotaPorId(mascotaId).ifPresent(turno::setMascota);
            if (empleadoId != null) {
                usuarioService.obtenerPorId(empleadoId).ifPresent(turno::setEmpleado);
            } else {
                turno.setEmpleado(null);
            }
            turnoService.guardar(turno);
        });
        auditoriaService.registrar(authentication.getName(), "MODIFICACION", "Turno",
            "Modificó turno ID: " + id + (servicio != null ? " - " + servicio : ""));
        redirectAttributes.addFlashAttribute("mensajeExito", "Turno actualizado correctamente");
        return "redirect:/turnos";
    }

    @GetMapping("/cambiar-estado/{id}/{estado}")
    public String cambiarEstado(@PathVariable Long id, @PathVariable String estado,
                                RedirectAttributes redirectAttributes,
                                Authentication authentication) {
        turnoService.obtenerPorId(id).ifPresent(turno -> {
            turno.setEstado(estado);
            turnoService.guardar(turno);
        });
        auditoriaService.registrar(authentication.getName(), "TURNO", "Turno",
            "Cambió estado turno ID: " + id + " a " + estado);
        redirectAttributes.addFlashAttribute("mensajeExito", "Estado actualizado");
        return "redirect:/turnos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes,
                           Authentication authentication) {
        auditoriaService.registrar(authentication.getName(), "ELIMINACION", "Turno",
            "Eliminó turno ID: " + id);
        turnoService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Turno eliminado correctamente");
        return "redirect:/turnos";
    }
}