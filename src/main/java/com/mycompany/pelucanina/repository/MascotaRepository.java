package com.mycompany.pelucanina.repository;

import com.mycompany.pelucanina.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {
    
    // Métodos automáticos heredados de JpaRepository
    
    // Métodos de consulta personalizados (opcionales)
    // Spring genera automáticamente la implementación según el nombre del método:
    
    // Buscar mascotas por nombre (contiene)
	List<Mascota> findByNombreContainingIgnoreCase(String nombre);
	List<Mascota> findByUnduenio_NombreContainingIgnoreCase(String nombre);
    
    // Buscar mascotas por raza
    // List<Mascota> findByRaza(Raza raza);
    
    // Buscar mascotas alérgicas
    // List<Mascota> findByAlergico(String alergico);
    
}