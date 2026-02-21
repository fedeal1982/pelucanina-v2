package com.mycompany.pelucanina.controller;

import com.mycompany.pelucanina.model.MascotaEliminada;
import com.mycompany.pelucanina.service.MascotaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/papelera")
public class PapeleraController {

    private final MascotaService mascotaService;

    public PapeleraController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    /**
     * Mostrar papelera
     * GET http://localhost:8080/papelera
     */
    @GetMapping
    public String verPapelera(Model model) {
        List<MascotaEliminada> mascotasEliminadas = mascotaService.obtenerMascotasEliminadas();
        model.addAttribute("mascotasEliminadas", mascotasEliminadas);
        return "papelera/lista";
    }

    /**
     * Restaurar mascota desde la papelera
     * GET http://localhost:8080/papelera/restaurar/1
     */
    @GetMapping("/restaurar/{id}")
    public String restaurarMascota(@PathVariable Long id, 
                                   RedirectAttributes redirectAttributes) {
        try {
            mascotaService.restaurarMascota(id);
            redirectAttributes.addFlashAttribute("mensajeExito", 
                "Mascota restaurada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", 
                "Error al restaurar: " + e.getMessage());
        }
        
        return "redirect:/papelera";
    }

    /**
     * Eliminar permanentemente de la papelera
     * GET http://localhost:8080/papelera/eliminar/{id}
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarPermanentemente(@PathVariable Long id, 
                                         RedirectAttributes redirectAttributes) {
        try {
            mascotaService.eliminarPermanentemente(id);
            redirectAttributes.addFlashAttribute("mensajeExito", 
                "Registro eliminado permanentemente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", 
                "Error al eliminar: " + e.getMessage());
        }
        
        return "redirect:/papelera";
    }

    /**
     * Vaciar toda la papelera
     * GET http://localhost:8080/papelera/vaciar
     */
    @GetMapping("/vaciar")
    public String vaciarPapelera(RedirectAttributes redirectAttributes) {
        try {
            long total = mascotaService.contarMascotasEliminadas();
            
            if (total == 0) {
                redirectAttributes.addFlashAttribute("mensajeError", 
                    "La papelera ya está vacía");
            } else {
                mascotaService.vaciarPapelera();
                redirectAttributes.addFlashAttribute("mensajeExito", 
                    "Papelera vaciada correctamente. Se eliminaron " + total + " registros");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", 
                "Error al vaciar papelera: " + e.getMessage());
        }
        
        return "redirect:/papelera";
    }
}