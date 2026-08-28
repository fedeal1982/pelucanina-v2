package com.mycompany.pelucanina.repository;

import com.mycompany.pelucanina.model.SolicitudTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudTurnoRepository extends JpaRepository<SolicitudTurno, Long> {
    List<SolicitudTurno> findAllByOrderByFechaSolicitudDesc();
    List<SolicitudTurno> findByEstadoOrderByFechaSolicitudDesc(String estado);
}