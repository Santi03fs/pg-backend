package com.pg.backend.repository;

import com.pg.backend.model.Fase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface FaseRepository extends JpaRepository<Fase, Long> {
    List<Fase> findByIdObra(Long idObra);
    List<Fase> findByIdObraOrderByNumeroAsc(Long idObra);

    @Transactional
    @Modifying
    @Query("DELETE FROM Fase f WHERE f.idObra = :idObra")
    void deleteByIdObra(@Param("idObra") Long idObra);

    @Transactional
    @Modifying
    @Query("DELETE FROM Fase f WHERE f.idObra IS NOT NULL AND f.idObra NOT IN (SELECT o.id FROM Obra o)")
    void deleteOrphanFases();
}
