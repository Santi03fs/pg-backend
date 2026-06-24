package com.pg.backend.repository;

import com.pg.backend.model.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long> {
    List<Partida> findByIdObra(Long idObra);
    List<Partida> findByIdObraOrderByNumeroAsc(Long idObra);
}
