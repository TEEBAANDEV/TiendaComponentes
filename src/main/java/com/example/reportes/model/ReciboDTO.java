package com.example.reportes.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

public class ReciboDTO {

    private Long idRecibo;
    private Long idVenta;
    private Long idUsuario;
    private Double montoTotal;
    private String metodoPago;
    private LocalDateTime fechaEmision;
}
