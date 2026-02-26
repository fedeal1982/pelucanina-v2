package com.mycompany.pelucanina.service;

import com.mycompany.pelucanina.model.Turno;
import com.mycompany.pelucanina.repository.TurnoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepository;

    public TurnoService(TurnoRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    public List<Turno> obtenerTodos() {
        return turnoRepository.findAllByOrderByFechaHoraAsc();
    }

    public List<Turno> obtenerPorEstado(String estado) {
        return turnoRepository.findByEstadoOrderByFechaHoraAsc(estado);
    }

    public List<Turno> obtenerSemanaActual() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicioSemana = ahora.with(WeekFields.of(Locale.forLanguageTag("es-AR")).dayOfWeek(), 1)
                .withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finSemana = inicioSemana.plusDays(6).withHour(23).withMinute(59).withSecond(59);
        return turnoRepository.findByFechaHoraBetween(inicioSemana, finSemana);
    }

    public List<Turno> obtenerProximos7Dias() {
        LocalDateTime ahora = LocalDateTime.now();
        return turnoRepository.findByFechaHoraBetween(ahora, ahora.plusDays(7));
    }

    public Optional<Turno> obtenerPorId(Long id) {
        return turnoRepository.findById(id);
    }

    public Turno guardar(Turno turno) {
        return turnoRepository.save(turno);
    }

    public void eliminar(Long id) {
        turnoRepository.deleteById(id);
    }

    public List<Turno> obtenerPorMascota(Integer mascotaId) {
        return turnoRepository.findByMascotaId(mascotaId);
    }
}