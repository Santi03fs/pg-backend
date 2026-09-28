package com.pg.backend.repository;

import com.pg.backend.model.EntregaCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntregaCuentaRepository extends JpaRepository<EntregaCuenta, Long> {
    List<EntregaCuenta> findByIdObra(Long idObra);
}
