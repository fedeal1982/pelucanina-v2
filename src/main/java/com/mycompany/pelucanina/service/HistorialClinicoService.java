package com.mycompany.pelucanina.service;

import com.mycompany.pelucanina.model.HistorialClinico;
import com.mycompany.pelucanina.repository.HistorialClinicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistorialClinicoService {

    private final HistorialClinicoRepository historialRepository;

    public HistorialClinicoService(HistorialClinicoRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    public List<HistorialClinico> obtenerPorMascota(Integer mascotaId) {
        return historialRepository.findByMascotaNumClienteOrderByFechaConsultaDesc(mascotaId);
    }

    public Optional<HistorialClinico> obtenerPorId(Long id) {
        return historialRepository.findById(id);
    }

    public HistorialClinico guardar(HistorialClinico historial) {
        return historialRepository.save(historial);
    }

    public void eliminar(Long id) {
        historialRepository.deleteById(id);
    }
}