package com.mycompany.pelucanina.service;

import com.mycompany.pelucanina.repository.TurnoRepository;
import com.mycompany.pelucanina.model.Turno;
import com.mycompany.pelucanina.model.Mascota;
import com.mycompany.pelucanina.model.MascotaEliminada;
import com.mycompany.pelucanina.repository.MascotaEliminadaRepository;
import com.mycompany.pelucanina.repository.MascotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mycompany.pelucanina.repository.HistorialClinicoRepository;
import com.mycompany.pelucanina.repository.VacunaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar las operaciones de Mascotas
 * Equivalente a la Controladora de JavaFX
 */
@Service
@Transactional
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final MascotaEliminadaRepository mascotaEliminadaRepository;
    private final HistorialClinicoRepository historialClinicoRepository;
    private final VacunaRepository vacunaRepository;
    private final TurnoRepository turnoRepository;

    // Inyección de dependencias por constructor
    public MascotaService(MascotaRepository mascotaRepository,
            MascotaEliminadaRepository mascotaEliminadaRepository,
            HistorialClinicoRepository historialClinicoRepository,
            VacunaRepository vacunaRepository,
            TurnoRepository turnoRepository) {
        this.mascotaRepository = mascotaRepository;
        this.mascotaEliminadaRepository = mascotaEliminadaRepository;
        this.historialClinicoRepository = historialClinicoRepository;
        this.vacunaRepository = vacunaRepository;
        this.turnoRepository = turnoRepository;
    }

    // ========== OPERACIONES DE MASCOTA ==========
    
    /**
     * Guardar una nueva mascota
     */
    public Mascota guardarMascota(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    /**
     * Obtener todas las mascotas
     */
    public List<Mascota> obtenerTodasLasMascotas() {
        return mascotaRepository.findAll();
    }

    /**
     * Buscar mascota por ID
     */
    public Optional<Mascota> obtenerMascotaPorId(Integer id) {
        return mascotaRepository.findById(id);
    }

    /**
     * Buscar mascotas por nombre
     */
    public List<Mascota> buscarPorNombre(String nombre) {
        List<Mascota> porNombreMascota = mascotaRepository.findByNombreContainingIgnoreCase(nombre);
        List<Mascota> porNombreDuenio = mascotaRepository.findByUnduenio_NombreContainingIgnoreCase(nombre);

        // Combinar sin duplicados
        List<Mascota> resultado = new java.util.ArrayList<>(porNombreMascota);
        for (Mascota m : porNombreDuenio) {
            if (!resultado.contains(m)) {
                resultado.add(m);
            }
        }
        return resultado;
    }

    /**
     * Actualizar una mascota existente
     */
    public Mascota actualizarMascota(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    /**
     * Eliminar mascota (mover a papelera)
     */
    public void eliminarMascota(Integer id) {
        Optional<Mascota> mascotaOpt = mascotaRepository.findById(id);
        if (mascotaOpt.isPresent()) {
            Mascota mascota = mascotaOpt.get();

            // Verificar si tiene turnos asociados
            List<Turno> turnos = turnoRepository.findByMascotaId(id);
            if (!turnos.isEmpty()) {
                throw new RuntimeException("No se puede eliminar la mascota porque tiene turnos asociados. Eliminá los turnos primero.");
            }

            // Eliminar historial y vacunas
            historialClinicoRepository.deleteAll(
                historialClinicoRepository.findByMascotaNumClienteOrderByFechaConsultaDesc(id));
            vacunaRepository.deleteAll(
                vacunaRepository.findByMascotaNumClienteOrderByFechaAplicacionDesc(id));

            // Crear copia en papelera
            MascotaEliminada mascotaEliminada = new MascotaEliminada(mascota);
            mascotaEliminadaRepository.save(mascotaEliminada);

            // Eliminar la mascota original
            mascotaRepository.deleteById(id);
        }
    }

    /**
     * Contar total de mascotas
     */
    public long contarMascotas() {
        return mascotaRepository.count();
    }

 

    // ========== OPERACIONES DE PAPELERA ==========
    
    /**
     * Obtener todas las mascotas eliminadas
     */
    public List<MascotaEliminada> obtenerMascotasEliminadas() {
        return mascotaEliminadaRepository.findAllByOrderByFechaEliminacionDesc();
    }

    /**
     * Restaurar mascota desde la papelera
     */
    public void restaurarMascota(Long idEliminada) {
        Optional<MascotaEliminada> mascotaEliminadaOpt = 
            mascotaEliminadaRepository.findById(idEliminada);
        
        if (mascotaEliminadaOpt.isPresent()) {
            MascotaEliminada mascotaEliminada = mascotaEliminadaOpt.get();
            
            
            // Crear nueva mascota restaurada
            Mascota mascota = new Mascota();
            mascota.setNombre(mascotaEliminada.getNombre());
            mascota.setColor(mascotaEliminada.getColor());
            mascota.setAlergico(mascotaEliminada.getAlergico());
            mascota.setAtencionEspecial(mascotaEliminada.getAtencionEspecial());
            mascota.setObservaciones(mascotaEliminada.getObservaciones());
            mascota.setEspecie(mascotaEliminada.getEspecie());
            mascota.setFechaNacimiento(mascotaEliminada.getFechaNacimiento());
            
            // Recrear dueño
            if (mascotaEliminada.getNombreDuenio() != null) {
                var duenio = new com.mycompany.pelucanina.model.Duenio();
                duenio.setNombre(mascotaEliminada.getNombreDuenio());
                duenio.setCelDuenio(mascotaEliminada.getCelDuenio());
                duenio.setDireccion(mascotaEliminada.getDireccion());
                duenio.setCodigoPostal(mascotaEliminada.getCodigoPostal());
                mascota.setUnduenio(duenio);
            }
            
            // Guardar mascota restaurada
            mascotaRepository.save(mascota);
            
            // Eliminar de papelera
            mascotaEliminadaRepository.deleteById(idEliminada);
        }
    }

    /**
     * Eliminar permanentemente de la papelera
     */
    public void eliminarPermanentemente(Long idEliminada) {
        mascotaEliminadaRepository.deleteById(idEliminada);
    }

    /**
     * Vaciar toda la papelera
     */
    public void vaciarPapelera() {
        mascotaEliminadaRepository.deleteAll();
    }

    /**
     * Contar mascotas en papelera
     */
    public long contarMascotasEliminadas() {
        return mascotaEliminadaRepository.count();
    }
}