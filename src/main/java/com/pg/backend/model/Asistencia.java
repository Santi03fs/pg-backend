package com.pg.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "asistencias")
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private Long idTrabajador;
    private Long idObra;
    private Boolean haAsistido = true;
    private Double horasTrabajadas;
    
    // --- LOS 3 CAMPOS NUEVOS PARA EL CUADRANTE ---
    private String horario;
    private String partida;
    private String descripcion;

    public Asistencia() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Long getIdTrabajador() { return idTrabajador; }
    public void setIdTrabajador(Long idTrabajador) { this.idTrabajador = idTrabajador; }

    public Long getIdObra() { return idObra; }
    public void setIdObra(Long idObra) { this.idObra = idObra; }

    public Boolean getHaAsistido() { return haAsistido; }
    public void setHaAsistido(Boolean haAsistido) { this.haAsistido = haAsistido; }

    public Double getHorasTrabajadas() { return horasTrabajadas; }
    public void setHorasTrabajadas(Double horasTrabajadas) { this.horasTrabajadas = horasTrabajadas; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getPartida() { return partida; }
    public void setPartida(String partida) { this.partida = partida; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}