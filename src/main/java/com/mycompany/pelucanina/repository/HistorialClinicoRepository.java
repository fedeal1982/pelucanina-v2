package com.mycompany.pelucanina.repository;

import com.mycompany.pelucanina.model.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialClinicoRepository extends JpaRepository<HistorialClinico, Long> {
    List<HistorialClinico> findByMascotaNumClienteOrderByFechaConsultaDesc(Integer mascotaId);
}