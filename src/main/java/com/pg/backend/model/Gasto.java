package com.pg.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "gastos_obra")
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idObra;
    private String categoria;
    private LocalDate fecha;
    private String descripcion;
    private String provTrabajador;
    private Double udsHoras;
    private Double precioNeto;
    private Double precioPvp;

    public Gasto() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdObra() { return idObra; }
    public void setIdObra(Long idObra) { this.idObra = idObra; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getProvTrabajador() { return provTrabajador; }
    public void setProvTrabajador(String provTrabajador) { this.provTrabajador = provTrabajador; }
    public Double getUdsHoras() { return udsHoras; }
    public void setUdsHoras(Double udsHoras) { this.udsHoras = udsHoras; }
    public Double getPrecioNeto() { return precioNeto; }
    public void setPrecioNeto(Double precioNeto) { this.precioNeto = precioNeto; }
    public Double getPrecioPvp() { return precioPvp; }
    public void setPrecioPvp(Double precioPvp) { this.precioPvp = precioPvp; }
}