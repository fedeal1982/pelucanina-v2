package com.mycompany.pelucanina.service;

import com.mycompany.pelucanina.model.Usuario;
import com.mycompany.pelucanina.repository.UsuarioRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JavaMailSender mailSender) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        if (!usuario.isActivo()) {
            throw new UsernameNotFoundException("Usuario desactivado: " + username);
        }
        return User.builder()
            .username(usuario.getUsername())
            .password(usuario.getPassword())
            .roles(usuario.getRol())
            .build();
    }

    public Usuario registrarUsuario(String username, String password, String nombreCompleto, String rol, String email) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new RuntimeException("El username ya existe");
        }
        if (email != null && !email.isEmpty() && usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuario.setEmail(email);
        return usuarioRepository.save(usuario);
    }

    public void cambiarPassword(String username, String passwordActual, String passwordNueva) {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
    }

    public void enviarEmailRecuperacion(String email, String baseUrl) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("No existe una cuenta con ese email"));
        String token = UUID.randomUUID().toString();
        usuario.setResetToken(token);
        usuario.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        usuarioRepository.save(usuario);

        String link = baseUrl + "/reset-password?token=" + token;
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(email);
        mensaje.setSubject("Recuperación de contraseña - Pelucanina");
        mensaje.setText("Hola " + usuario.getNombreCompleto() + ",\n\n"
            + "Recibimos una solicitud para restablecer tu contraseña.\n\n"
            + "Hacé clic en el siguiente link:\n" + link + "\n\n"
            + "Este link vence en 1 hora.\n\n"
            + "Si no solicitaste esto, ignorá este mensaje.\n\n"
            + "Saludos,\nEquipo Pelucanina");
        mailSender.send(mensaje);
    }

    public void resetearPassword(String token, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findByResetToken(token)
            .orElseThrow(() -> new RuntimeException("Token inválido"));
        if (usuario.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token ha expirado");
        }
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setResetToken(null);
        usuario.setResetTokenExpiry(null);
        usuarioRepository.save(usuario);
    }

    public List<Usuario> obtenerTodosLosUsuarios() { return usuarioRepository.findAll(); }
    public Optional<Usuario> obtenerPorUsername(String username) { return usuarioRepository.findByUsername(username); }
    public boolean existeUsername(String username) { return usuarioRepository.existsByUsername(username); }
    public long contarUsuarios() { return usuarioRepository.count(); }
    public Optional<Usuario> obtenerPorId(Long id) { return usuarioRepository.findById(id); }
    public Usuario guardar(Usuario usuario) { return usuarioRepository.save(usuario); }
    public void eliminar(Long id) { usuarioRepository.deleteById(id); }
}