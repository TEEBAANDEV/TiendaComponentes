package com.example.recibo.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class VentaDTO {

    private Long id;
    private Long idUsuario;
    private String nombreProducto;
    private List<DetalleVentaDTO> detalles; //WAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAkajsdhajksdhkdh
    private Double total;
    private LocalDateTime fecha;
    private String estado;
}
