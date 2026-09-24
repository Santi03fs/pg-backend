package com.pg.backend.repository;


import com.pg.backend.model.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    List<Asistencia> findByIdObraAndPartida(Long idObra, String partida);
    List<Asistencia> findByIdObra(Long idObra);

    @Transactional
    @Modifying
    @Query("DELETE FROM Asistencia a WHERE a.idObra = :idObra")
    void deleteByIdObra(@Param("idObra") Long idObra);

    @Transactional
    @Modifying
    @Query("DELETE FROM Asistencia a WHERE a.idObra IS NOT NULL AND a.idObra NOT IN (SELECT o.id FROM Obra o)")
    void deleteOrphanAsistencias();
}