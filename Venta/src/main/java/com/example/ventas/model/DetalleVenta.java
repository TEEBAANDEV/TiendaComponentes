package com.example.ventas.model;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "venta_detalles")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreProducto;
    private String descripcion;
    private int cantidad;
    private Double precioUnitario;
}
