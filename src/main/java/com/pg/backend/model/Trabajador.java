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
    
    // Horas diarias que trabaja (jornada laboral) y pago por día
    private Double horasJornada = 8.0;
    private Double pagoDiario = 0.0;

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

    public Double getHorasJornada() { return horasJornada; }
    public void setHorasJornada(Double horasJornada) { this.horasJornada = horasJornada; }

    public Double getPagoDiario() { return pagoDiario; }
    public void setPagoDiario(Double pagoDiario) { this.pagoDiario = pagoDiario; }
}