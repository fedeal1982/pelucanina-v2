package com.mycompany.pelucanina.repository;

import com.mycompany.pelucanina.model.Raza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Raza
 * 
 * JpaRepository proporciona automáticamente estos métodos:
 * - save(raza) - Guardar/actualizar
 * - findById(id) - Buscar por ID
 * - findAll() - Traer todas las razas
 * - deleteById(id) - Eliminar por ID
 * - count() - Contar registros
 * 
 * ¡No necesitas implementar nada!
 */
@Repository
public interface RazaRepository extends JpaRepository<Raza, Integer> {
    
    // Spring genera automáticamente la implementación
    // Puedes agregar métodos personalizados si necesitas:
    
    // Ejemplo: Buscar raza por nombre
    // Raza findByNombreRaza(String nombreRaza);
    
}