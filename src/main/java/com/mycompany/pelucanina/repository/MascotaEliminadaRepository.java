package com.mycompany.pelucanina.repository;

import com.mycompany.pelucanina.model.MascotaEliminada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MascotaEliminadaRepository extends JpaRepository<MascotaEliminada, Long> {
    
    // Métodos automáticos heredados de JpaRepository
    
    // Opcional: Ordenar por fecha de eliminación
    List<MascotaEliminada> findAllByOrderByFechaEliminacionDesc();
    
}