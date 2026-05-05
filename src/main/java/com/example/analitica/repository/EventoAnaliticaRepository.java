package com.example.analitica.repository;

import com.example.analitica.model.EventoAnalitica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoAnaliticaRepository extends JpaRepository<EventoAnaliticaRepository, Long> {
    List<EventoAnalitica> findByTipoEvento(String tipoEvento);
    long countByTipoEvento(String tipoEvento);
}
