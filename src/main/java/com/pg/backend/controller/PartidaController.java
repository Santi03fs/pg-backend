package com.pg.backend.controller;

import com.pg.backend.model.Partida;
import com.pg.backend.model.Asistencia;
import com.pg.backend.repository.PartidaRepository;
import com.pg.backend.repository.AsistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partidas")
@CrossOrigin(origins = "*")
public class PartidaController {

    @Autowired
    private PartidaRepository partidaRepository;

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private com.pg.backend.repository.ObraRepository obraRepository;

    @GetMapping
    public List<Partida> obtenerTodasLasPartidas() {
        List<com.pg.backend.model.Obra> obras = obraRepository.findAll();
        for (com.pg.backend.model.Obra obra : obras) {
            List<Partida> list = partidaRepository.findByIdObra(obra.getId());
            if (list.size() < 30) {
                for (int i = list.size() + 1; i <= 30; i++) {
                    Partida p = new Partida(obra.getId(), i, "Partida " + i);
                    partidaRepository.save(p);
                }
            }
        }
        return partidaRepository.findAll();
    }

    @GetMapping("/obra/{idObra}")
    public List<Partida> obtenerPartidasPorObra(@PathVariable Long idObra) {
        List<Partida> list = partidaRepository.findByIdObraOrderByNumeroAsc(idObra);
        if (list.size() < 30) {
            // Generate missing partidas up to 30
            for (int i = list.size() + 1; i <= 30; i++) {
                Partida p = new Partida(idObra, i, "Partida " + i);
                partidaRepository.save(p);
            }
            list = partidaRepository.findByIdObraOrderByNumeroAsc(idObra);
        }
        return list;
    }

    @PutMapping("/{id}")
    public Partida actualizarPartida(@PathVariable Long id, @RequestBody Partida partidaDetalles) {
        Partida partida = partidaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));
        
        String oldNombre = partida.getNombre();
        String newNombre = partidaDetalles.getNombre();
        Long idObra = partida.getIdObra();

        // 1. Update the partida name
        partida.setNombre(newNombre);
        Partida guardada = partidaRepository.save(partida);

        // 2. Propagate rename to all existing attendance records for this worksite
        if (idObra != null && oldNombre != null && !oldNombre.equals(newNombre)) {
            List<Asistencia> asistencias = asistenciaRepository.findByIdObraAndPartida(idObra, oldNombre);
            for (Asistencia asis : asistencias) {
                asis.setPartida(newNombre);
                asistenciaRepository.save(asis);
            }
        }

        return guardada;
    }
}
