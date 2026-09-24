package com.pg.backend.controller;

import com.pg.backend.model.Obra;
import com.pg.backend.repository.AsistenciaRepository;
import com.pg.backend.repository.GastoRepository;
import com.pg.backend.repository.ObraRepository;
import com.pg.backend.repository.PartidaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/obras")
public class ObraController {

    @Autowired
    private ObraRepository obraRepository;

    @Autowired
    private PartidaRepository partidaRepository;

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private GastoRepository gastoRepository;

    @PostConstruct
    public void limpiarHuerfanosInicial() {
        try {
            asistenciaRepository.deleteOrphanAsistencias();
            gastoRepository.deleteOrphanGastos();
            partidaRepository.deleteOrphanPartidas();
            System.out.println("Limpieza inicial de registros huérfanos completada correctamente.");
        } catch (Exception e) {
            System.err.println("Advertencia al limpiar huérfanos en arranque: " + e.getMessage());
        }
    }

    @GetMapping
    public List<Obra> obtenerTodasLasObras() {
        return obraRepository.findAll();
    }

    @PostMapping
    public Obra guardarObra(@RequestBody Obra obra) {
        boolean esNueva = (obra.getId() == null);
        Obra obraGuardada = obraRepository.save(obra);
        
        if (esNueva) {
            // Limpieza de seguridad por si el ID reutilizado tenía restos huérfanos anteriores
            try {
                asistenciaRepository.deleteByIdObra(obraGuardada.getId());
                gastoRepository.deleteByIdObra(obraGuardada.getId());
                partidaRepository.deleteByIdObra(obraGuardada.getId());
            } catch (Exception ignored) {}

            String[] plantillas = {
                "Albañilería",
                "Carpintería de madera",
                "Carpintería de aluminio / PVC",
                "Cerrajería",
                "Fontanería",
                "Electricidad",
                "Climatización / Aire acondicionado",
                "Pladur / Tabiquería seca",
                "Pintura",
                "Solados y alicatados",
                "Impermeabilización",
                "Cubiertas y tejados",
                "Demoliciones / Desescombro",
                "Montajes y mantenimiento",
                "Fachadas / Revestimientos"
            };

            // Generar 30 partidas con la plantilla inicial de 15 y el resto enumeradas
            for (int i = 1; i <= 30; i++) {
                String nombrePartida = (i <= plantillas.length) ? plantillas[i - 1] : ("Partida " + i);
                com.pg.backend.model.Partida p = new com.pg.backend.model.Partida(obraGuardada.getId(), i, nombrePartida);
                partidaRepository.save(p);
            }
        }
        
        return obraGuardada;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Obra> actualizarObra(@PathVariable Long id, @RequestBody Obra obraActualizada) {
        return obraRepository.findById(id).map(obra -> {
            if (obraActualizada.getCliente() != null) {
                obra.setCliente(obraActualizada.getCliente());
            }
            if (obraActualizada.getNombreObra() != null) {
                obra.setNombreObra(obraActualizada.getNombreObra());
            }
            if (obraActualizada.getFechaInicio() != null) {
                obra.setFechaInicio(obraActualizada.getFechaInicio());
            }
            Obra guardada = obraRepository.save(obra);
            return ResponseEntity.ok(guardada);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/estado")
    public Obra cambiarEstado(@PathVariable Long id, @RequestBody boolean finalizada) {
        Obra obra = obraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Obra no encontrada"));

        obra.setFinalizada(finalizada);
        return obraRepository.save(obra);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarObra(@PathVariable Long id) {
        return obraRepository.findById(id).map(obra -> {
            // 1. Candado de seguridad: ¿Está acabada?
            if (!obra.isFinalizada()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No se puede eliminar la obra porque no está marcada como finalizada.");
            }
            
            // 2. Si está acabada, eliminamos sus partidas, gastos y asistencias asociados para no dejar huérfanos
            try {
                asistenciaRepository.deleteByIdObra(id);
                gastoRepository.deleteByIdObra(id);
                partidaRepository.deleteByIdObra(id);
                obraRepository.delete(obra);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al eliminar la obra: " + e.getMessage());
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/limpiar-huerfanos")
    public ResponseEntity<String> limpiarHuerfanosManual() {
        try {
            asistenciaRepository.deleteOrphanAsistencias();
            gastoRepository.deleteOrphanGastos();
            partidaRepository.deleteOrphanPartidas();
            return ResponseEntity.ok("Registros huérfanos eliminados correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}