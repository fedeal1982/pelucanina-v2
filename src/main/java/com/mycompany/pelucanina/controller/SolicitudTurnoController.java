package com.mycompany.pelucanina.controller;

import com.mycompany.pelucanina.model.SolicitudTurno;
import com.mycompany.pelucanina.repository.SolicitudTurnoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SolicitudTurnoController {

    private final SolicitudTurnoRepository solicitudTurnoRepository;

    public SolicitudTurnoController(SolicitudTurnoRepository solicitudTurnoRepository) {
        this.solicitudTurnoRepository = solicitudTurnoRepository;
    }

    @PostMapping("/solicitudes")
    public ResponseEntity<?> crearSolicitud(@RequestBody Map<String, String> datos) {
        try {
            SolicitudTurno solicitud = new SolicitudTurno();
            solicitud.setNombre(datos.get("nombre"));
            solicitud.setEmail(datos.get("email"));
            solicitud.setTelefono(datos.get("telefono"));
            solicitud.setMascota(datos.get("mascota"));
            solicitud.setServicio(datos.get("servicio"));
            solicitud.setFechaPreferida(LocalDate.parse(datos.get("fecha")));
            solicitud.setMensaje(datos.get("mensaje"));
            solicitudTurnoRepository.save(solicitud);
            return ResponseEntity.ok(Map.of("mensaje", "Solicitud recibida correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/solicitudes")
    public ResponseEntity<List<SolicitudTurno>> listar() {
        return ResponseEntity.ok(solicitudTurnoRepository.findAllByOrderByFechaSolicitudDesc());
    }
}