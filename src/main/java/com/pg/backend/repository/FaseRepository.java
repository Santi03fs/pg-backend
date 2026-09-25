package com.pg.backend.repository;

import com.pg.backend.model.Fase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaseRepository extends JpaRepository<Fase, Long> {
    List<Fase> findByIdObra(Long idObra);
}
