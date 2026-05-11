package com.example.analitica.repository;

import com.example.analitica.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Resenarepository extends JpaRepository<Resena, Long> {
    List<Resena> findByProductoId(Long productoId);
    List<Resena> findByUsuarioId(Long usuarioId);
    long countByProductoId(Long productoId);

}
