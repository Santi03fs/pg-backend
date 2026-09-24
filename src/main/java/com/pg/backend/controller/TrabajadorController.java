package com.pg.backend.controller;

import com.pg.backend.model.Trabajador;
import com.pg.backend.repository.TrabajadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trabajadores")
@CrossOrigin(origins = "*")
public class TrabajadorController {

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    // Obtener todos los trabajadores
    @GetMapping
    public List<Trabajador> obtenerTodosLosTrabajadores() {
        return trabajadorRepository.findAll();
    }

    // Crear un trabajador nuevo
    @PostMapping
    public Trabajador guardarTrabajador(@RequestBody Trabajador trabajador) {
        return trabajadorRepository.save(trabajador);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTrabajador(@PathVariable Long id) {
        try {
            trabajadorRepository.deleteById(id);
            return ResponseEntity.ok().build(); // Todo ha ido bien
        } catch (Exception e) {
            // Si falla (seguramente porque el trabajador ya tiene horas en la tabla
            // asistencias)
            return ResponseEntity.badRequest().body("No se puede eliminar: El trabajador tiene horas registradas.");
        }
    }
}