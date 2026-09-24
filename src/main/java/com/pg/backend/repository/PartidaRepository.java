package com.pg.backend.repository;

import com.pg.backend.model.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long> {
    List<Partida> findByIdObra(Long idObra);
    List<Partida> findByIdObraOrderByNumeroAsc(Long idObra);

    @Transactional
    @Modifying
    @Query("DELETE FROM Partida p WHERE p.idObra = :idObra")
    void deleteByIdObra(@Param("idObra") Long idObra);

    @Transactional
    @Modifying
    @Query("DELETE FROM Partida p WHERE p.idObra IS NOT NULL AND p.idObra NOT IN (SELECT o.id FROM Obra o)")
    void deleteOrphanPartidas();
}
