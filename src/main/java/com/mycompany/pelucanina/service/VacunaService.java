package com.mycompany.pelucanina.service;

import com.mycompany.pelucanina.model.Vacuna;
import com.mycompany.pelucanina.repository.VacunaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VacunaService {

    private final VacunaRepository vacunaRepository;

    public VacunaService(VacunaRepository vacunaRepository) {
        this.vacunaRepository = vacunaRepository;
    }

    public List<Vacuna> obtenerPorMascota(Integer mascotaId) {
        return vacunaRepository.findByMascotaNumClienteOrderByFechaAplicacionDesc(mascotaId);
    }

    public Optional<Vacuna> obtenerPorId(Long id) {
        return vacunaRepository.findById(id);
    }

    public Vacuna guardar(Vacuna vacuna) {
        return vacunaRepository.save(vacuna);
    }

    public void eliminar(Long id) {
        vacunaRepository.deleteById(id);
    }
}