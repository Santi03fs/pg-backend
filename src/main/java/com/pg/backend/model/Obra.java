package com.pg.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "obras")
public class Obra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cliente;
    private String nombreObra;
    private LocalDate fechaInicio;

    public Obra() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getNombreObra() { return nombreObra; }
    public void setNombreObra(String nombreObra) { this.nombreObra = nombreObra; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
}