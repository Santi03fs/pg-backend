package com.pg.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "entregas_cuenta")
public class EntregaCuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idObra;
    private String descripcion;
    private Double cantidad;

    public EntregaCuenta() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdObra() { return idObra; }
    public void setIdObra(Long idObra) { this.idObra = idObra; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getCantidad() { return cantidad; }
    public void setCantidad(Double cantidad) { this.cantidad = cantidad; }
}
