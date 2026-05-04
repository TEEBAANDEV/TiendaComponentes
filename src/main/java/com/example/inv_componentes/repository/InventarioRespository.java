package com.example.inv_componentes.repository;

import com.example.inv_componentes.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioRespository extends JpaRepository<Inventario,Long> {
}
