package com.mycompany.pelucanina.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_turno")
public class SolicitudTurno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(length = 100)
    private String mascota;

    @Column(nullable = false, length = 100)
    private String servicio;

    @Column(nullable = false)
    private LocalDate fechaPreferida;

    @Column(length = 500)
    private String mensaje;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    public SolicitudTurno() {
        this.estado = "PENDIENTE";
        this.fechaSolicitud = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getMascota() { return mascota; }
    public void setMascota(String mascota) { this.mascota = mascota; }
    public String getServicio() { return servicio; }
    public void setServicio(String servicio) { this.servicio = servicio; }
    public LocalDate getFechaPreferida() { return fechaPreferida; }
    public void setFechaPreferida(LocalDate fechaPreferida) { this.fechaPreferida = fechaPreferida; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
}
