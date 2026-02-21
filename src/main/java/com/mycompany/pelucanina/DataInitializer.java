package com.mycompany.pelucanina;

import com.mycompany.pelucanina.model.Raza;
import com.mycompany.pelucanina.repository.RazaRepository;
import com.mycompany.pelucanina.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Esta clase se ejecuta automáticamente al iniciar la aplicación
 * Inicializa datos necesarios: razas y usuario admin
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RazaRepository razaRepository;
    private final UsuarioService usuarioService;

    public DataInitializer(RazaRepository razaRepository, UsuarioService usuarioService) {
        this.razaRepository = razaRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Inicializar razas
        if (razaRepository.count() == 0) {
            System.out.println("Inicializando razas...");
            
            razaRepository.save(new Raza("Labrador", "Perro grande, amigable y enérgico"));
            razaRepository.save(new Raza("Golden Retriever", "Perro grande, gentil y confiable"));
            razaRepository.save(new Raza("Pastor Alemán", "Perro grande, inteligente y versátil"));
            razaRepository.save(new Raza("Bulldog Francés", "Perro pequeño, adaptable y juguetón"));
            razaRepository.save(new Raza("Chihuahua", "Perro muy pequeño, alerta y vivaz"));
            razaRepository.save(new Raza("Poodle", "Perro inteligente, activo y elegante"));
            razaRepository.save(new Raza("Beagle", "Perro mediano, amigable y curioso"));
            razaRepository.save(new Raza("Rottweiler", "Perro grande, robusto y confiado"));
            razaRepository.save(new Raza("Yorkshire Terrier", "Perro pequeño, valiente y enérgico"));
            razaRepository.save(new Raza("Mestizo", "Mezcla de razas"));
            
            System.out.println("✅ Razas inicializadas correctamente");
        }
        
        // Crear usuario administrador por defecto
        if (usuarioService.contarUsuarios() == 0) {
            System.out.println("Creando usuario administrador...");
            
            usuarioService.registrarUsuario(
                "admin",           // username
                "admin123",        // password
                "Administrador",   // nombre completo
                "ADMIN"           // rol
            );
            
            System.out.println("✅ Usuario admin creado");
            System.out.println("   Username: admin");
            System.out.println("   Password: admin123");
        }
    }
}