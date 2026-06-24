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

    @Autowired
    private com.pg.backend.repository.PartidaRepository partidaRepository;

    @GetMapping
    public List<Obra> obtenerTodasLasObras() {
        return obraRepository.findAll();
    }

    @PostMapping
    public Obra guardarObra(@RequestBody Obra obra) {
        boolean esNueva = (obra.getId() == null);
        Obra obraGuardada = obraRepository.save(obra);
        
        if (esNueva) {
            // Generate 30 enumerated partidas automatically
            for (int i = 1; i <= 30; i++) {
                com.pg.backend.model.Partida p = new com.pg.backend.model.Partida(obraGuardada.getId(), i, "Partida " + i);
                partidaRepository.save(p);
            }
        }
        
        return obraGuardada;
    }

    @PutMapping("/{id}/estado")
    public Obra cambiarEstado(@PathVariable Long id, @RequestBody boolean finalizada) {
        Obra obra = obraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Obra no encontrada"));

        obra.setFinalizada(finalizada);
        return obraRepository.save(obra);
    }
}