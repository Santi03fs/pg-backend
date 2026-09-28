package com.pg.backend.controller;

import com.pg.backend.model.EntregaCuenta;
import com.pg.backend.repository.EntregaCuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entregas")
@CrossOrigin(origins = "*")
public class EntregaCuentaController {

    @Autowired
    private EntregaCuentaRepository entregaCuentaRepository;

    @GetMapping("/obra/{idObra}")
    public List<EntregaCuenta> getEntregasByObra(@PathVariable Long idObra) {
        return entregaCuentaRepository.findByIdObra(idObra);
    }

    @PostMapping
    public EntregaCuenta createEntrega(@RequestBody EntregaCuenta entrega) {
        return entregaCuentaRepository.save(entrega);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEntrega(@PathVariable Long id) {
        if (!entregaCuentaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        entregaCuentaRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
