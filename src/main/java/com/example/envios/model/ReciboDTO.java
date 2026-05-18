package com.example.envios.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReciboDTO {

    private Long idRecibo;
    private Long idVenta;
    private Long idUsuario;
    private String nombreProducto;
    private Double montoTotal;
    private LocalDateTime fechaEmision;
}
