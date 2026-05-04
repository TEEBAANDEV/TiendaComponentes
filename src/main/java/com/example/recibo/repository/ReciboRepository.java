package com.example.recibo.repository;

import com.example.recibo.model.Recibo;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReciboRepository extends JpaRepository<Recibo, Long> {
}
