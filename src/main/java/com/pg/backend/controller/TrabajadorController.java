package com.pg.backend.controller;

import com.pg.backend.model.Trabajador;
import com.pg.backend.repository.TrabajadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}