package com.pg.backend.controller;

import com.pg.backend.model.Gasto;
import com.pg.backend.repository.GastoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gastos")
@CrossOrigin(origins = "http://localhost:3000")
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
}