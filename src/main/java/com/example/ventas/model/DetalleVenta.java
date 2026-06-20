package com.example.ventas.model;

import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "venta_detalles")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    private String nombreProducto;
    private String descripcion;
    private int cantidad;
    private Double precioUnitario;
}
