package com.mycompany.pelucanina.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mascotas")
public class Mascota {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num_cliente")
    private Integer numCliente;
    
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
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_duenio")
    private Duenio unduenio;
    
    @ManyToOne
    @JoinColumn(name = "id_raza")
    private Raza raza;

    // Constructores
    public Mascota() {
    }

    public Mascota(String nombre, String color, String alergico, String atencionEspecial, 
                   String observaciones, Duenio unduenio, Raza raza) {
        this.nombre = nombre;
        this.color = color;
        this.alergico = alergico;
        this.atencionEspecial = atencionEspecial;
        this.observaciones = observaciones;
        this.unduenio = unduenio;
        this.raza = raza;
    }

    // Getters y Setters
    public Integer getNumCliente() {
        return numCliente;
    }

    public void setNumCliente(Integer numCliente) {
        this.numCliente = numCliente;
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

    public Duenio getUnduenio() {
        return unduenio;
    }

    public void setUnduenio(Duenio unduenio) {
        this.unduenio = unduenio;
    }

    public Raza getRaza() {
        return raza;
    }

    public void setRaza(Raza raza) {
        this.raza = raza;
    }
}