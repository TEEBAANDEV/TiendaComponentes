package com.example.recibo.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VentaDTO {

    private Long id;
    private Long idUsuario;
    private Double total;
    private LocalDateTime fecha;
    private String estado;
}
