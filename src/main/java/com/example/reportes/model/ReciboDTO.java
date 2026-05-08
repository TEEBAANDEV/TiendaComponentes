package com.example.reportes.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReciboDTO {

    private Long idRecibo;
    private Long idVenta;
    private Long idUsuario;
    private String nombreProducto;
    private Double montoTotal;
    private String metodoPago;
    private LocalDateTime fechaEmision;
}
