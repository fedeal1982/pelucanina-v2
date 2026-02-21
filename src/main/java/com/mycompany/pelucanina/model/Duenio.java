package com.mycompany.pelucanina.model;

import jakarta.persistence.*;

@Entity
@Table(name = "duenios")
public class Duenio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_duenio")
    private Integer idDuenio;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "cel_duenio", length = 20)
    private String celDuenio;
    
    @Column(length = 200)
    private String direccion;
    
    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;

    // Constructores
    public Duenio() {
    }

    public Duenio(String nombre, String celDuenio, String direccion, String codigoPostal) {
        this.nombre = nombre;
        this.celDuenio = celDuenio;
        this.direccion = direccion;
        this.codigoPostal = codigoPostal;
    }

    // Getters y Setters
    public Integer getIdDuenio() {
        return idDuenio;
    }

    public void setIdDuenio(Integer idDuenio) {
        this.idDuenio = idDuenio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
}