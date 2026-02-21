package com.mycompany.pelucanina.service;

import com.mycompany.pelucanina.model.Mascota;
import com.mycompany.pelucanina.model.MascotaEliminada;
import com.mycompany.pelucanina.model.Raza;
import com.mycompany.pelucanina.repository.MascotaEliminadaRepository;
import com.mycompany.pelucanina.repository.MascotaRepository;
import com.mycompany.pelucanina.repository.RazaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final RazaRepository razaRepository;
    private final MascotaEliminadaRepository mascotaEliminadaRepository;

    // Inyección de dependencias por constructor
    public MascotaService(MascotaRepository mascotaRepository, 
                         RazaRepository razaRepository,
                         MascotaEliminadaRepository mascotaEliminadaRepository) {
        this.mascotaRepository = mascotaRepository;
        this.razaRepository = razaRepository;
        this.mascotaEliminadaRepository = mascotaEliminadaRepository;
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

    // ========== OPERACIONES DE RAZAS ==========
    
    /**
     * Obtener todas las razas
     */
    public List<Raza> obtenerTodasLasRazas() {
        return razaRepository.findAll();
    }

    /**
     * Buscar raza por ID
     */
    public Optional<Raza> obtenerRazaPorId(Integer id) {
        return razaRepository.findById(id);
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
            
            // Buscar la raza por nombre
            Optional<Raza> razaOpt = razaRepository.findAll().stream()
                .filter(r -> r.getNombreRaza().equals(mascotaEliminada.getNombreRaza()))
                .findFirst();
            
            // Crear nueva mascota restaurada
            Mascota mascota = new Mascota();
            mascota.setNombre(mascotaEliminada.getNombre());
            mascota.setColor(mascotaEliminada.getColor());
            mascota.setAlergico(mascotaEliminada.getAlergico());
            mascota.setAtencionEspecial(mascotaEliminada.getAtencionEspecial());
            mascota.setObservaciones(mascotaEliminada.getObservaciones());
            mascota.setRaza(razaOpt.orElse(null));
            
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