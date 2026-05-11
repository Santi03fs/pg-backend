package com.pg.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "trabajadores")
public class Trabajador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String dni;
    private String telefono;
    private String estado = "Activo";

    public Trabajador() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}