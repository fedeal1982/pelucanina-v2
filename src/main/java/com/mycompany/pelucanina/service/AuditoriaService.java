package com.mycompany.pelucanina.service;

import com.mycompany.pelucanina.model.AuditoriaLog;
import com.mycompany.pelucanina.repository.AuditoriaLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditoriaService {

    private final AuditoriaLogRepository auditoriaLogRepository;

    public AuditoriaService(AuditoriaLogRepository auditoriaLogRepository) {
        this.auditoriaLogRepository = auditoriaLogRepository;
    }

    public void registrar(String usuario, String accion, String entidad, String detalle) {
        AuditoriaLog log = new AuditoriaLog(usuario, accion, entidad, detalle);
        auditoriaLogRepository.save(log);
    }

    public List<AuditoriaLog> obtenerTodos() {
        return auditoriaLogRepository.findAllByOrderByFechaDesc();
    }
}