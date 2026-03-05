package com.mycompany.pelucanina;

import com.mycompany.pelucanina.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioService usuarioService;

    public DataInitializer(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (usuarioService.contarUsuarios() == 0) {
            usuarioService.registrarUsuario(
                "admin",
                "admin123",
                "Administrador",
                "ADMIN"
            );
            System.out.println("✅ Usuario admin creado");
        }
    }
}