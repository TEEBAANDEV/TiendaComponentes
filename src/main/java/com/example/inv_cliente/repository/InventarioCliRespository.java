package com.example.inv_cliente.repository;

import com.example.inv_cliente.model.Inventario_cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventarioCliRespository extends JpaRepository<Inventario_cliente,Long> {
    List<Inventario_cliente> findByIdUsuario(Long idUsuario);
}
