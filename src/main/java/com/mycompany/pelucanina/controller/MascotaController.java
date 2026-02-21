package com.mycompany.pelucanina.controller;

import com.mycompany.pelucanina.model.Duenio;
import com.mycompany.pelucanina.model.Mascota;
import com.mycompany.pelucanina.model.Raza;
import com.mycompany.pelucanina.service.MascotaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    /**
     * Mostrar lista de mascotas
     * GET http://localhost:8080/mascotas
     */
    @GetMapping
    public String listarMascotas(@RequestParam(required = false) String buscar, Model model) {
        List<Mascota> mascotas;

        if (buscar != null && !buscar.trim().isEmpty()) {
            mascotas = mascotaService.buscarPorNombre(buscar.trim());
            model.addAttribute("buscar", buscar);
        } else {
            mascotas = mascotaService.obtenerTodasLasMascotas();
        }

        model.addAttribute("mascotas", mascotas);
        return "mascotas/lista";
    }

    /**
     * Mostrar formulario para nueva mascota
     * GET http://localhost:8080/mascotas/nueva
     */
    @GetMapping("/nueva")
    public String mostrarFormularioNuevo(Model model) {
        Mascota mascota = new Mascota();
        mascota.setUnduenio(new Duenio()); // Inicializar dueño vacío
        
        List<Raza> razas = mascotaService.obtenerTodasLasRazas();
        
        model.addAttribute("mascota", mascota);
        model.addAttribute("razas", razas);
        model.addAttribute("esNuevo", true);
        
        return "mascotas/formulario";
    }

    /**
     * Guardar nueva mascota
     * POST http://localhost:8080/mascotas
     */
    @PostMapping
    public String guardarMascota(@ModelAttribute Mascota mascota, 
                                @RequestParam Integer razaId,
                                RedirectAttributes redirectAttributes) {
        try {
            // Asignar la raza seleccionada
            Optional<Raza> raza = mascotaService.obtenerRazaPorId(razaId);
            raza.ifPresent(mascota::setRaza);
            
            // Guardar mascota (el dueño se guarda en cascada)
            mascotaService.guardarMascota(mascota);
            
            redirectAttributes.addFlashAttribute("mensajeExito", 
                "Mascota guardada correctamente");
            
            return "redirect:/mascotas";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", 
                "Error al guardar: " + e.getMessage());
            return "redirect:/mascotas/nueva";
        }
    }

    /**
     * Mostrar formulario para editar mascota
     * GET http://localhost:8080/mascotas/editar/1
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Optional<Mascota> mascotaOpt = mascotaService.obtenerMascotaPorId(id);
        
        if (mascotaOpt.isEmpty()) {
            return "redirect:/mascotas";
        }
        
        List<Raza> razas = mascotaService.obtenerTodasLasRazas();
        
        model.addAttribute("mascota", mascotaOpt.get());
        model.addAttribute("razas", razas);
        model.addAttribute("esNuevo", false);
        
        return "mascotas/formulario";
    }

    /**
     * Actualizar mascota existente
     * POST http://localhost:8080/mascotas/editar/{id}
     */
    @PostMapping("/editar/{id}")
    public String actualizarMascota(@PathVariable Integer id,
                                   @ModelAttribute Mascota mascota,
                                   @RequestParam Integer razaId,
                                   RedirectAttributes redirectAttributes) {
        try {
            mascota.setNumCliente(id);
            
            Optional<Raza> raza = mascotaService.obtenerRazaPorId(razaId);
            raza.ifPresent(mascota::setRaza);
            
            mascotaService.actualizarMascota(mascota);
            
            redirectAttributes.addFlashAttribute("mensajeExito", 
                "Mascota actualizada correctamente");
            
            return "redirect:/mascotas";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", 
                "Error al actualizar: " + e.getMessage());
            return "redirect:/mascotas/editar/" + id;
        }
    }

    /**
     * Eliminar mascota (mover a papelera)
     * GET http://localhost:8080/mascotas/eliminar/1
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarMascota(@PathVariable Integer id, 
                                 RedirectAttributes redirectAttributes) {
        try {
            mascotaService.eliminarMascota(id);
            redirectAttributes.addFlashAttribute("mensajeExito", 
                "Mascota movida a la papelera");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", 
                "Error al eliminar: " + e.getMessage());
        }
        
        return "redirect:/mascotas";
    }
    
 // GET /mascotas/{id}
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Integer id, Model model) {
        Optional<Mascota> mascotaOpt = mascotaService.obtenerMascotaPorId(id);
        
        if (mascotaOpt.isEmpty()) {
            return "redirect:/mascotas";
        }
        
        model.addAttribute("mascota", mascotaOpt.get());
        return "mascotas/detalle";
    }
}