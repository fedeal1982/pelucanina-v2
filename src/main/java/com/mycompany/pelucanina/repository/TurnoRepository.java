package com.mycompany.pelucanina.repository;

import com.mycompany.pelucanina.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    List<Turno> findAllByOrderByFechaHoraAsc();

    List<Turno> findByEstadoOrderByFechaHoraAsc(String estado);

    @Query("SELECT t FROM Turno t WHERE t.fechaHora BETWEEN :inicio AND :fin ORDER BY t.fechaHora ASC")
    List<Turno> findByFechaHoraBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT t FROM Turno t WHERE t.mascota.numCliente = :mascotaId ORDER BY t.fechaHora DESC")
    List<Turno> findByMascotaId(@Param("mascotaId") Integer mascotaId);
}