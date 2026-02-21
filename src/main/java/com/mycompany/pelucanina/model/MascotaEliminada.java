package com.mycompany.pelucanina.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mascotas_eliminadas")
public class MascotaEliminada {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "num_cliente_original")
    private Integer numClienteOriginal;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(length = 50)
    private String color;
    
    @Column(length = 2)
    private String alergico;
    
    @Column(name = "atencion_especial", length = 2)
    private String atencionEspecial;
    
    @Column(length = 500)
    private String observaciones;
    
    @Column(name = "fecha_eliminacion")
    private LocalDateTime fechaEliminacion;
    
    @Column(name = "nombre_duenio", length = 100)
    private String nombreDuenio;
    
    @Column(name = "cel_duenio", length = 20)
    private String celDuenio;
    
    @Column(length = 200)
    private String direccion;
    
    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;
    
    @Column(name = "nombre_raza", length = 100)
    private String nombreRaza;
    
    @Column(name = "eliminado_por", length = 100)
    private String eliminadoPor;

    // Constructores
    public MascotaEliminada() {
    }

    public MascotaEliminada(Mascota mascota) {
        this.numClienteOriginal = mascota.getNumCliente();
        this.nombre = mascota.getNombre();
        this.color = mascota.getColor();
        this.alergico = mascota.getAlergico();
        this.atencionEspecial = mascota.getAtencionEspecial();
        this.observaciones = mascota.getObservaciones();
        this.fechaEliminacion = LocalDateTime.now();
        
        if (mascota.getUnduenio() != null) {
            this.nombreDuenio = mascota.getUnduenio().getNombre();
            this.celDuenio = mascota.getUnduenio().getCelDuenio();
            this.direccion = mascota.getUnduenio().getDireccion();
            this.codigoPostal = mascota.getUnduenio().getCodigoPostal();
        }
        
        if (mascota.getRaza() != null) {
            this.nombreRaza = mascota.getRaza().getNombreRaza();
        }
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumClienteOriginal() {
        return numClienteOriginal;
    }

    public void setNumClienteOriginal(Integer numClienteOriginal) {
        this.numClienteOriginal = numClienteOriginal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAlergico() {
        return alergico;
    }

    public void setAlergico(String alergico) {
        this.alergico = alergico;
    }

    public String getAtencionEspecial() {
        return atencionEspecial;
    }

    public void setAtencionEspecial(String atencionEspecial) {
        this.atencionEspecial = atencionEspecial;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaEliminacion() {
        return fechaEliminacion;
    }

    public void setFechaEliminacion(LocalDateTime fechaEliminacion) {
        this.fechaEliminacion = fechaEliminacion;
    }

    public String getNombreDuenio() {
        return nombreDuenio;
    }

    public void setNombreDuenio(String nombreDuenio) {
        this.nombreDuenio = nombreDuenio;
    }

    public String getCelDuenio() {
        return celDuenio;
    }

    public void setCelDuenio(String celDuenio) {
        this.celDuenio = celDuenio;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getNombreRaza() {
        return nombreRaza;
    }

    public void setNombreRaza(String nombreRaza) {
        this.nombreRaza = nombreRaza;
    }

    public String getEliminadoPor() {
        return eliminadoPor;
    }

    public void setEliminadoPor(String eliminadoPor) {
        this.eliminadoPor = eliminadoPor;
    }
}