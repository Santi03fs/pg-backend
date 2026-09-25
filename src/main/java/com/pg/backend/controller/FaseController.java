package com.pg.backend.controller;

import com.pg.backend.model.Fase;
import com.pg.backend.repository.FaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fases")
public class FaseController {

    @Autowired
    private FaseRepository faseRepository;

    @GetMapping
    public List<Fase> getAllFases() {
        return faseRepository.findAll();
    }

    @GetMapping("/obra/{idObra}")
    public List<Fase> getFasesByObra(@PathVariable Long idObra) {
        return faseRepository.findByIdObra(idObra);
    }

    @PostMapping
    public Fase createFase(@RequestBody Fase fase) {
        return faseRepository.save(fase);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fase> updateFase(@PathVariable Long id, @RequestBody Fase faseDetails) {
        return faseRepository.findById(id).map(fase -> {
            fase.setNombre(faseDetails.getNombre());
            return ResponseEntity.ok(faseRepository.save(fase));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFase(@PathVariable Long id) {
        return faseRepository.findById(id).map(fase -> {
            faseRepository.delete(fase);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
