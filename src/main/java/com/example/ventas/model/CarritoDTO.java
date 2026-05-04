package com.example.ventas.model;


import lombok.Data;

@Data
public class CarritoDTO {

    private Long id;
    private Long idUsuario;
    private Long idProducto;
    private int cantidad;
}
