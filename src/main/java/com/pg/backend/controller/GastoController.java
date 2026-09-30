package com.pg.backend.controller;

import com.pg.backend.model.Gasto;
import com.pg.backend.repository.GastoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gastos")
public class GastoController {

    @Autowired
    private GastoRepository gastoRepository;

    @GetMapping
    public List<Gasto> obtenerTodosLosGastos() {
        return gastoRepository.findAll();
    }

    @PostMapping
    public Gasto guardarGasto(@RequestBody Gasto gasto) {
        return gastoRepository.save(gasto);
    }

    @PutMapping("/{id}")
    public Gasto actualizarGasto(@PathVariable Long id, @RequestBody Gasto gastoActualizado) {
        return gastoRepository.findById(id).map(gasto -> {
            gasto.setIdObra(gastoActualizado.getIdObra());
            gasto.setCategoria(gastoActualizado.getCategoria());
            gasto.setPartida(gastoActualizado.getPartida());
            gasto.setFase(gastoActualizado.getFase());
            gasto.setFecha(gastoActualizado.getFecha());
            gasto.setDescripcion(gastoActualizado.getDescripcion());
            gasto.setProvTrabajador(gastoActualizado.getProvTrabajador());
            gasto.setUdsHoras(gastoActualizado.getUdsHoras());
            gasto.setPrecioNeto(gastoActualizado.getPrecioNeto());
            gasto.setPrecioPvp(gastoActualizado.getPrecioPvp());
            return gastoRepository.save(gasto);
        }).orElseGet(() -> {
            gastoActualizado.setId(id);
            return gastoRepository.save(gastoActualizado);
        });
    }

    @DeleteMapping("/{id}")
    public void eliminarGasto(@PathVariable Long id) {
        gastoRepository.deleteById(id);
    }
}