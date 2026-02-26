package com.mycompany.pelucanina.controller;

import com.mycompany.pelucanina.model.Turno;
import com.mycompany.pelucanina.service.MascotaService;
import com.mycompany.pelucanina.service.TurnoService;
import com.mycompany.pelucanina.service.UsuarioService;
import org.springframework.format.annotation.DateTimeFormat;
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

    public TurnoController(TurnoService turnoService, MascotaService mascotaService, UsuarioService usuarioService) {
        this.turnoService = turnoService;
        this.mascotaService = mascotaService;
        this.usuarioService = usuarioService;
    }

    // GET /turnos - lista de turnos
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

    // GET /turnos/calendario - vista calendario
    @GetMapping("/calendario")
    public String calendario(Model model) {
        model.addAttribute("turnos", turnoService.obtenerProximos7Dias());
        return "turnos/calendario";
    }

    // GET /turnos/nuevo - formulario nuevo turno
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("mascotas", mascotaService.obtenerTodasLasMascotas());
        model.addAttribute("empleados", usuarioService.obtenerTodosLosUsuarios());
        return "turnos/formulario";
    }

    // POST /turnos/nuevo - guardar nuevo turno
    @PostMapping("/nuevo")
    public String guardar(@RequestParam Integer mascotaId,
                          @RequestParam(required = false) Long empleadoId,
                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fechaHora,
                          @RequestParam(required = false) String servicio,
                          @RequestParam(required = false) BigDecimal precio,
                          @RequestParam(defaultValue = "PENDIENTE") String estado,
                          @RequestParam(required = false) String observaciones,
                          RedirectAttributes redirectAttributes) {
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
            redirectAttributes.addFlashAttribute("mensajeExito", "Turno creado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al crear el turno: " + e.getMessage());
        }
        return "redirect:/turnos";
    }

    // GET /turnos/editar/{id}
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

    // POST /turnos/editar/{id}
    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @RequestParam Integer mascotaId,
                             @RequestParam(required = false) Long empleadoId,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fechaHora,
                             @RequestParam(required = false) String servicio,
                             @RequestParam(required = false) BigDecimal precio,
                             @RequestParam(defaultValue = "PENDIENTE") String estado,
                             @RequestParam(required = false) String observaciones,
                             RedirectAttributes redirectAttributes) {
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
        redirectAttributes.addFlashAttribute("mensajeExito", "Turno actualizado correctamente");
        return "redirect:/turnos";
    }

    // GET /turnos/cambiar-estado/{id}/{estado}
    @GetMapping("/cambiar-estado/{id}/{estado}")
    public String cambiarEstado(@PathVariable Long id, @PathVariable String estado,
                                RedirectAttributes redirectAttributes) {
        turnoService.obtenerPorId(id).ifPresent(turno -> {
            turno.setEstado(estado);
            turnoService.guardar(turno);
        });
        redirectAttributes.addFlashAttribute("mensajeExito", "Estado actualizado");
        return "redirect:/turnos";
    }

    // GET /turnos/eliminar/{id}
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        turnoService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Turno eliminado correctamente");
        return "redirect:/turnos";
    }
}