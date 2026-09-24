package com.pg.backend.repository;

import com.pg.backend.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {
    List<Gasto> findByIdObra(Long idObra);

    @Transactional
    @Modifying
    @Query("DELETE FROM Gasto g WHERE g.idObra = :idObra")
    void deleteByIdObra(@Param("idObra") Long idObra);

    @Transactional
    @Modifying
    @Query("DELETE FROM Gasto g WHERE g.idObra IS NOT NULL AND g.idObra NOT IN (SELECT o.id FROM Obra o)")
    void deleteOrphanGastos();
}