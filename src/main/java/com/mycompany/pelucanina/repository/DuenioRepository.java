package com.mycompany.pelucanina.repository;

import com.mycompany.pelucanina.model.Duenio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DuenioRepository extends JpaRepository<Duenio, Integer> {
    
    // Métodos automáticos heredados de JpaRepository
    
}