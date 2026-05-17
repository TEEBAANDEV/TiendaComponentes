package com.example.recibo.model;

import lombok.Data;

@Data
public class DetalleVentaDTO {

    private String nombreProducto;
    private String descripcion;
    private int cantidad;
    private Double precioUnitario;
}
