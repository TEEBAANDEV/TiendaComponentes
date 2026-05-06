package com.example.ventas.respository;

import com.example.ventas.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta,Long> {

}
