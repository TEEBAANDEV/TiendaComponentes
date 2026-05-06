package com.example.inv_componentes.repository;

import com.example.inv_componentes.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventarioRespository extends JpaRepository<Inventario,Long> {
    Optional<Inventario> findByIdProducto(Long idProducto);
}
