package com.pg.backend.controller;

import com.pg.backend.model.Asistencia;
import com.pg.backend.repository.AsistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaController {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @GetMapping
    public List<Asistencia> obtenerTodasLasAsistencias() {
        return asistenciaRepository.findAll();
    }

    @PostMapping
    public Asistencia guardarAsistencia(@RequestBody Asistencia asistencia) {
        return asistenciaRepository.save(asistencia);
    }

    @PostMapping("/batch")
    public List<Asistencia> guardarAsistenciasEnLote(@RequestBody List<Asistencia> asistencias) {
        for (Asistencia a : asistencias) {
            if (a.getId() == null && a.getIdTrabajador() != null && a.getFecha() != null) {
                List<Asistencia> existentes = asistenciaRepository.findByIdTrabajadorAndFecha(a.getIdTrabajador(), a.getFecha());
                for (Asistencia e : existentes) {
                    boolean mismaObra = (e.getIdObra() == null && a.getIdObra() == null) ||
                                       (e.getIdObra() != null && e.getIdObra().equals(a.getIdObra()));
                    if (mismaObra) {
                        a.setId(e.getId());
                        break;
                    }
                }
            }
        }
        return asistenciaRepository.saveAll(asistencias);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAsistencia(@PathVariable Long id) {
        try {
            asistenciaRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar la asistencia: " + e.getMessage());
        }
    }
}