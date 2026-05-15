package com.pg.backend.controller;

import com.pg.backend.model.Obra;
import com.pg.backend.repository.ObraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/obras")
@CrossOrigin(origins = "*")
public class ObraController {

    @Autowired
    private ObraRepository obraRepository;

    @GetMapping
    public List<Obra> obtenerTodasLasObras() {
        return obraRepository.findAll();
    }

    @PostMapping
    public Obra guardarObra(@RequestBody Obra obra) {
        return obraRepository.save(obra);
    }

    @PutMapping("/{id}/estado")
    public Obra cambiarEstado(@PathVariable Long id, @RequestBody boolean finalizada) {
        Obra obra = obraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Obra no encontrada"));

        obra.setFinalizada(finalizada);
        return obraRepository.save(obra);
    }
}