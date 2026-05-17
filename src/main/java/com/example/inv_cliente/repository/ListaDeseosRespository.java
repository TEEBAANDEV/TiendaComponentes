package com.example.inv_cliente.repository;

import com.example.inv_cliente.model.ListaDeseados;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListaDeseosRespository extends JpaRepository<ListaDeseados,Long> {
    List<ListaDeseados> findByIdUsuario(Long idUsuario);

    @Transactional
    void deleteByIdUsuario(Long idUsuario);

}
